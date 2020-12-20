package com.microee.traditex.liqui.app.core.disk;

import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.microee.plugin.thread.ThreadPoolFactoryLow;
import com.microee.stacks.redis.support.RedisHash;
import com.microee.traditex.inbox.oem.constrants.OrderSideEnum;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.liqui.app.components.DaylightSavingTimeComponent;
import com.microee.traditex.liqui.app.components.LiquiRiskSettings;
import com.microee.traditex.liqui.app.components.OrderBookAdapter;
import com.microee.traditex.liqui.app.components.TranferPriceService;
import com.microee.traditex.liqui.app.constrants.LiquiPrefixs;
import com.microee.traditex.liqui.app.mappers.OrderbookStreamMapper;
import com.microee.traditex.liqui.app.producer.RedisProducer;
import com.microee.traditex.liqui.app.service.OrderbookStreamService;
import com.microee.traditex.liqui.oem.OrderBook;
import com.microee.traditex.liqui.oem.enums.OrderBookSkipCodeEnum;
import com.microee.traditex.liqui.oem.models.LiquiRiskStrategySettings;

// 流动性摆盘服务
@Component
public class DiskTableService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskTableService.class);

    private static ThreadPoolFactoryLow threadPool =
            ThreadPoolFactoryLow.newInstance("traditex-liqui-挂单线程池");

    @Autowired
    private LiquiRiskSettings liquiConf;

    @Autowired
    private RedisProducer redisProducer;

    @Autowired
    private TranferPriceService tranferService;

    @Autowired
    private DiskOrderService diskOrderService;

    @Autowired
    private OrderBookAdapter orderBookAdapter;

    @Autowired
    private OrderbookStreamService orderbookStreamService;

    @Autowired
    private DaylightSavingTimeComponent daylightSavingTime;

    @Autowired
    private OrderbookStreamMapper orderbookStreamMapper;

    @Autowired
    private RedisHash redisHash;
    
    // 盘口挂单锁
    private final AtomicBoolean locked = new AtomicBoolean(false);

    private final AtomicBoolean placingOrder = new AtomicBoolean(false);

    public void process(JSONObject orderbooks) {
        String orderBookId = orderbooks.getString("the-order-book-id");
        VENDER vender = VENDER.get(orderbooks.getString("vender"));
        String connid = orderbooks.getString("connid"); // orderbook-connid
        OrderBook orderbook = orderBookAdapter.adapter(orderBookId, connid, vender,
                orderbooks.getJSONObject("message"));
        if (orderbook == null) {
            return;
        }

        // 如果下单中 或者 得锁失败
        if (placingOrder.get() || !this.locked.compareAndSet(false, true)) {
            redisProducer.broadcaseOrderBook(orderbook); // 通过 redis 广播 orderbook
            orderbookStreamService.save(vender, OrderBookSkipCodeEnum.SKIPED_IF_HAVE_UNREVOKED,
                    orderbook);
            return;
        }

        try{
            // 如果还有挂单存在就不能继续挂单
            String key = LiquiPrefixs.DISK_ORDER_PREFIX;
            if (redisHash.sizeOf(key) > 0) {
                redisProducer.broadcaseOrderBook(orderbook); // 通过 redis 广播 orderbook
                orderbookStreamService.save(vender, OrderBookSkipCodeEnum.SKIPED_IF_HAVE_UNREVOKED,
                        orderbook);
                return;
            }

            redisProducer.broadcaseOrderBook(orderbook); // 通过 redis 广播 orderbook
            LiquiRiskStrategySettings settings = liquiConf.getCachedStrategySettings(orderbook.symbol);
            OrderBookSkipCodeEnum skipCode = this.getSkipCode(orderbook.symbol, settings);
            LOGGER.info("skipCode: symbol={}, skipCode={}", orderbook.symbol, skipCode);
            if (skipCode.equals(OrderBookSkipCodeEnum.SKIPED_IF_SELL_NOT_OPEN)
                    || skipCode.equals(OrderBookSkipCodeEnum.SKIPED_IF_BUY_NOT_OPEN)
                    || skipCode.equals(OrderBookSkipCodeEnum.FOUCED)) {
                if (orderbookStreamMapper.selectByBookId(orderBookId) != null) {
                    LOGGER.info("重复的orderbook推送已忽略: orderBookId={}", orderBookId);
                    return;
                }
                JSONArray asksOrders = new JSONArray();
                if (!skipCode.equals(OrderBookSkipCodeEnum.SKIPED_IF_SELL_NOT_OPEN)) {
                    for (int i = 0; i < orderbook.asks.length(); i++) {
                        JSONObject linkJSONObject = tranferService.tranferPrice(
                                String.format("%s-%s-%s", orderBookId, OrderSideEnum.SELL.name(), i),
                                connid, vender, orderbook.symbol, OrderSideEnum.SELL,
                                orderbook.asks.getJSONArray(i));
                        if (linkJSONObject != null) {
                            linkJSONObject.put("skipCode", skipCode);
                            asksOrders.put(linkJSONObject);
                        }
                    }
                }
                JSONArray bidsOrders = new JSONArray();
                if (!skipCode.equals(OrderBookSkipCodeEnum.SKIPED_IF_BUY_NOT_OPEN)) {
                    for (int i = 0; i < orderbook.bids.length(); i++) {
                        JSONObject linkJSONObject =
                                tranferService.tranferPrice(
                                        String.format("%s-%s-%s", orderBookId, OrderSideEnum.BUY.name(),
                                                i),
                                        connid, vender, orderbook.symbol, OrderSideEnum.BUY,
                                        orderbook.bids.getJSONArray(i));
                        if (linkJSONObject != null) {
                            linkJSONObject.put("skipCode", skipCode);
                            bidsOrders.put(linkJSONObject);
                        }
                    }
                }
                if (asksOrders.length() == 0 && bidsOrders.length() == 0) {
                    // 删除挂单锁
                    placingOrder.set(false);
                    this.locked.set(false);
                } else {
                    placingOrder.set(true);
                    // 根据买卖订单挂单, 挂单成功后将所有订单放入redis, 为避免自成交本次一次挂单前先保证先保证上一次的挂单全部撤掉
                    threadPool.pool().submit(() -> {
                        diskOrderService.createDiskOrder(connid, vender, orderbook, asksOrders,
                                bidsOrders);
                        placingOrder.set(false);
                        return true;
                    });
                }
            } else {
                // 删除挂单锁
                this.locked.set(false);
                placingOrder.set(false);
            }
            // 保存orderbook
            orderbookStreamService.save(vender, skipCode, orderbook);
        } catch (Exception e) {
            LOGGER.error("盘口挂单出现错误", e);
            this.locked.set(false);
            placingOrder.set(false);
        }
    }

    public OrderBookSkipCodeEnum getSkipCode(String symbol, LiquiRiskStrategySettings settings) {
        if (!daylightSavingTime.isDiskTradOpen()) {
            // 周末停盘
            return OrderBookSkipCodeEnum.SKIPED_AT_WEEKEND;
        }
        // 判断买卖盘开启状态
        if (settings == null) {
            return OrderBookSkipCodeEnum.SKIPED_UNSUPPORTD_SYMBOL;
        }
        if (settings.getSellState().equals(1) && settings.getBuyState().equals(1)) {
            // 买卖全开
            return OrderBookSkipCodeEnum.FOUCED;
        }
        if (!settings.getSellState().equals(1) && !settings.getBuyState().equals(1)) {
            // 买卖全关
            return OrderBookSkipCodeEnum.SKIPED_IF_NOT_OPEN;
        }
        if (settings.getSellState().equals(1) || settings.getBuyState().equals(1)) {
            if (!settings.getSellState().equals(1)) {
                // 卖盘未开
                return OrderBookSkipCodeEnum.SKIPED_IF_SELL_NOT_OPEN;
            } 
            // 买盘未开
            return OrderBookSkipCodeEnum.SKIPED_IF_BUY_NOT_OPEN;
        }
        return OrderBookSkipCodeEnum.FOUCED;
    }

    // 解除盘口挂单锁
    public synchronized void unlock() {
        this.locked.set(false);
    }

    public synchronized boolean islock() {
        return this.locked.get();
    }

    public synchronized void unlockPlacingOrder(){
        placingOrder.set(false);
    }

    public synchronized boolean isPlacingOrder(){
        return placingOrder.get();
    }

}
