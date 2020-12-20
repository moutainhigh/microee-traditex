package com.microee.traditex.liqui.app.core.disk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.commons.UUIDObject;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.response.exception.RestException;
import com.microee.traditex.inbox.oem.constrants.OrderSideEnum;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.inbox.oem.hbitex.HBiTexHttpResult;
import com.microee.traditex.inbox.rmi.TradiTexHBiTexOrderClient;
import com.microee.traditex.liqui.app.components.AccountBalanceService;
import com.microee.traditex.liqui.app.components.DiskOrderMoneyLimitted;
import com.microee.traditex.liqui.app.components.DiskOrderMoneyLimitted.DiskAccountBalance;
import com.microee.traditex.liqui.app.components.LiquiRiskSettings;
import com.microee.traditex.liqui.app.core.conn.ConnectBootstrap;
import com.microee.traditex.liqui.app.core.conn.ConnectServiceMap;
import com.microee.traditex.liqui.app.core.disk.DiskOrderParam.PriceAndQuantityIncrRate;
import com.microee.traditex.liqui.app.core.revoke.RevokeService;
import com.microee.traditex.liqui.app.producer.RedisProducer;
import com.microee.traditex.liqui.app.props.LiquisConfProps;
import com.microee.traditex.liqui.app.props.conf.HBiTexAccountConf;
import com.microee.traditex.liqui.app.service.DiskOrderTableService;
import com.microee.traditex.liqui.app.service.delay.DelayQueryOrderStatsService;
import com.microee.traditex.liqui.oem.OrderBook;
import com.microee.traditex.liqui.oem.enums.LiquiAccountType;
import com.microee.traditex.liqui.oem.models.DiskOrderTable;
import com.microee.traditex.liqui.oem.models.LiquiRiskStrategySettings;

// 流动性挂单服务
@Service
public class DiskOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskOrderService.class);

    @Autowired
    private TradiTexHBiTexOrderClient hbiTexOrderClient;

    @Autowired
    private ConnectBootstrap setupConnectService;

    @Autowired
    private RevokeService liquiRevokeService;

    @Autowired
    private DelayQueryOrderStatsService delayQueryOrderStatsService;

    @Autowired
    private RedisProducer redisProducer;

    @Autowired
    private DiskOrderTableService diskOrderTableService;

    @Autowired
    private ConnectServiceMap connectServiceMap;

    @Autowired
    private LiquiRiskSettings liquiConf;

    @Autowired
    private LiquisConfProps liquisConfProps;

    @Autowired
    private DiskOrderMoneyLimitted diskOrderMoneyLimitted;

    @Autowired
    private AccountBalanceService balanceService;

    public void createDiskOrder(String orderbookConnid, VENDER vender, OrderBook orderbook,
            JSONArray asksOrders, JSONArray bidsOrders) {
        String orderbalanceConnid = setupConnectService.getConnidDiskConnid();
        if (orderbalanceConnid == null) {
            return;
        }
        String batchId = UUID.randomUUID().toString();
        List<DiskOrderTable> diskTableList = new ArrayList<>();
        // 每挂挂1单计算买卖盘资金占用上限, 超过上限则不挂
        for (int i = 0; i < asksOrders.length(); i++) {
            if (liquisConfProps.getDebugOneOrder() && i > 0) {
                continue;
            }
            DiskOrderTable diskOrder = sendCreateOrderRequest(i, batchId, orderbalanceConnid,
                    asksOrders.getJSONObject(i), "sell-limit");
            if (diskOrder != null) {
                diskTableList.add(diskOrder);
            }
            
        }
        for (int i = 0; i < bidsOrders.length(); i++) {
            if (liquisConfProps.getDebugOneOrder() && i > 0) {
                continue;
            }
            DiskOrderTable diskOrder = sendCreateOrderRequest(i, batchId, orderbalanceConnid,
                    bidsOrders.getJSONObject(i), "buy-limit");
            if (diskOrder != null) {
                diskTableList.add(diskOrder);
            }
        }
        liquiRevokeService.add(liquiRevokeService); // 在延时队列中添加一个延迟任务, 2秒后 pending 队列的订单全部撤单
        diskOrderTableService.save(diskTableList);
    }

    public DiskOrderTable sendCreateOrderRequest(int pick, String batchId,
            String orderbalanceConnid, JSONObject linkJSONObject, String orderType) {
        if (orderbalanceConnid == null) {
            LOGGER.warn("流动性摆盘挂单挂单连接尚未建立: batchId={}, linkJSONObject={}", batchId,
                    linkJSONObject.toString());
            return null;
        }
        JSONObject param = linkJSONObject.getJSONObject("target");
        String orderBookId = linkJSONObject.getJSONObject("source").getString("theOrderBookId");
        HBiTexAccountConf accountConf = connectServiceMap.getAccountByConnId(orderbalanceConnid,
                new TypeReference<HBiTexAccountConf>() {});
        String diskAccount = accountConf.getUid();
        String usdtPrice = param.getString("USDT_PRICE");
        String usdCurrencyPricing = param.getString("USD_JPY_RATE");
        String usdtUSDRate = param.getString("USDT_USD_RATE");
        String symbol = param.getString("symbol").toLowerCase();
        LiquiRiskStrategySettings settings = liquiConf.getCachedStrategySettings("btcusdt");
        if (settings == null) {
            LOGGER.warn("未能取得摆盘策略配置: batchId={}, settings=N/a", batchId);
            return null;
        }
        String side = param.getString("side");
        int diskPricePrec = settings.getPricePrecson(); // 价格精度
        int diskAmountPrec = settings.getQuantityPrecson(); // 数量精度
        BigDecimal targetAmount = BigDecimal.valueOf(param.getDouble("amount"));
        BigDecimal targetPrice = BigDecimal.valueOf(param.getDouble("price"));
        BigDecimal amountDecimal = null;
        BigDecimal priceDecimal = null;
        String clientOrderId = UUIDObject.get().toString();
        PriceAndQuantityIncrRate priceAndQuantityIncrRate = DiskOrderParam.getDiskOrderIncrRate(orderType, pick, settings);
        if(null == priceAndQuantityIncrRate){
            LOGGER.error("未获取到档位({})对应的流动性配置, orderType: {}", pick + 1, orderType);
            return null;
        }

        if (priceAndQuantityIncrRate.getQuantityIncr().compareTo(BigDecimal.ZERO) <= 0 ||
                priceAndQuantityIncrRate.getPriceIncr().compareTo(BigDecimal.ZERO) <= 0) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("不搬运盘口档位({}): priceAndQuantityIncrRate={}, diskOrder={}, settings={}", pick + 1,
                        HttpAssets.toJsonString(priceAndQuantityIncrRate), "0配置忽略", HttpAssets.toJsonString(settings));
            }
            return null;
        }

        amountDecimal = targetAmount.multiply(priceAndQuantityIncrRate.getQuantityIncr()).setScale(diskAmountPrec, BigDecimal.ROUND_DOWN);
        priceDecimal = targetPrice.multiply(priceAndQuantityIncrRate.getPriceIncr()).setScale(diskPricePrec, BigDecimal.ROUND_DOWN);

        if (amountDecimal.compareTo(new BigDecimal("0.001")) == -1) {
            amountDecimal = new BigDecimal("0.001");
        }
        String amountString = amountDecimal.toPlainString();
        String priceString = priceDecimal.toPlainString();
        String resthost = accountConf.getResthost();
        DiskOrderParam diskOrderParam = new DiskOrderParam(symbol, side, orderType, amountString, priceString, clientOrderId);
        if (orderType.indexOf("buy") != -1) {
            DiskAccountBalance jpyBalance = balanceService.readAccountBalance(LiquiAccountType.DISK, "jpy", false); 
            if (diskOrderMoneyLimitted.isMoneyLimitted("jpy", priceDecimal.multiply(amountDecimal), jpyBalance, diskOrderParam)) {
                return null; 
            }
        }
        if (orderType.indexOf("sell") != -1) {
            DiskAccountBalance btcBalance = balanceService.readAccountBalance(LiquiAccountType.DISK, "btc", false);
            if (diskOrderMoneyLimitted.isAmountLimitted("btc", amountDecimal, btcBalance, diskOrderParam)) {
                return null;
            }
        }
        //@formatter:off
        linkJSONObject.put("distOrderParam", new JSONObject(HttpAssets.toJsonString(diskOrderParam)));
        linkJSONObject.put("config", new JSONObject().put("resthost", resthost).put("connid", orderbalanceConnid).put("uid", diskAccount));
        //@formatter:on
        HBiTexHttpResult<String> createOrderResult = null;
        try {
            // 流动性摆盘甩单
            createOrderResult = hbiTexOrderClient.create(orderbalanceConnid, diskOrderParam.clientOrderId,
                    diskOrderParam.orderType, diskOrderParam.symbol, diskOrderParam.price, diskOrderParam.amount, diskOrderParam.side, resthost).getData();
        } catch (RestException e) {
            // 流动性摆盘挂单超时
            delayQueryOrderStatsService.delayQuery(orderbalanceConnid, clientOrderId);
            redisProducer.broadcaseHttpNetwork(new JSONObject()
                    .put("title", "挂单-超时, 等1秒后查询订单详情看是否是挂单状态, 如是挂单状态, 加入撤单队列")  
                    .put("connid", orderbalanceConnid).put("clientOrderId", clientOrderId)
                    .put("linkJSONObject", linkJSONObject).put("errorMessage", e.getMessage()));
        } catch (ClassCastException e) {
            LOGGER.error("挂单类型转换异常: errorMessage={}", e.getMessage(), e);
        }
        
        String orderResultId = null;
        if (createOrderResult == null || !createOrderResult.isSuccess()) {
            orderResultId = "N/a";
            JSONObject createOrderResultJSON = null;
            if (createOrderResult != null) {
                if (createOrderResult.getErrCode().equals("invalid-client-order-id")) {
                    // 订单号重复加入撤单队列, 根据客户端订单号查询订单
                    delayQueryOrderStatsService.delayQuery(orderbalanceConnid, clientOrderId);
                } else if (createOrderResult.getErrCode().equals("order-limitorder-amount-max-error")) {
                    // 订单数量超过限制
                    
                }
                if (createOrderResult.getErrMsg() != null) {
                    if (createOrderResult.getErrMsg().length() > 400) {
                        createOrderResult.setErrMsg(createOrderResult.getErrMsg().substring(0,400));
                    }
                }
                createOrderResultJSON = new JSONObject(HttpAssets.toJsonString(createOrderResult));
            } else {
                // 根据客户端订单号查询订单
                // TODO
                delayQueryOrderStatsService.delayQuery(orderbalanceConnid, clientOrderId);
            }
            redisProducer.broadcaseHttpNetwork(
                    new JSONObject().put("title", "挂单-失败, 需判断是否是订单号重复, 如重复, 加入撤单队列")
                            .put("connid", orderbalanceConnid).put("clientOrderId", clientOrderId)
                            .put("createOrderResult", createOrderResultJSON)
                            .put("linkJSONObject", linkJSONObject));
        } else {
            orderResultId = createOrderResult.getData();
            liquiRevokeService.pending(orderbalanceConnid, VENDER.HBiTex,
                    createOrderResult.getData(), linkJSONObject.toString()); // 放入撤单队列
            redisProducer.broadcaseHttpNetwork(new JSONObject().put("title", "挂单-成功")
                    .put("connid", orderbalanceConnid).put("clientOrderId", clientOrderId)
                    .put("createOrderResult", createOrderResult.getData())
                    .put("linkJSONObject", linkJSONObject));
        }
        DiskOrderTable diskOrder = diskOrderTableService.buildDiskOrder(diskOrderParam.clientOrderId,
                orderResultId, VENDER.HBiJTex, diskOrderParam.symbol.toUpperCase(), OrderSideEnum.valueOf(diskOrderParam.side),
                String.valueOf(targetAmount), String.valueOf(targetPrice), usdCurrencyPricing,
                usdtUSDRate, diskOrderParam.amount, diskOrderParam.price, diskPricePrec, diskAmountPrec, diskOrderParam.orderType,
                diskAccount, "N/a", HttpAssets.toJsonString(createOrderResult), 0, orderBookId, usdtPrice);
        LOGGER.info("改价格: priceAndQuantityIncrRate={}, diskOrder={}, settings={}", 
                HttpAssets.toJsonString(priceAndQuantityIncrRate), HttpAssets.toJsonString(diskOrder), HttpAssets.toJsonString(settings));
        return diskOrder;
    }

}
