package com.microee.traditex.liqui.app.caches;

import java.math.BigDecimal;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.stacks.redis.support.Redis;
import com.microee.traditex.inbox.oem.constrants.OrderSideEnum;
import com.microee.traditex.liqui.app.constrants.LiquiPrefixs;
import com.microee.traditex.liqui.oem.OrderBook;

@Component
public class LiquiCachesComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiquiCachesComponent.class);
    
    @Autowired
    private Redis redis;
    
    // 缓存orderbook
    public String cacheOrderbook(OrderBook orderbook) {
        String _orderbook = HttpAssets.toJsonString(orderbook);
        redis.set(LiquiPrefixs.LIQUI_ORDER_BOOK, _orderbook);
        return _orderbook;
    }
    
    // 读取orderbook1档数据
    public BigDecimal readPick1Price(OrderSideEnum side,  OrderBook orderbook) {
        if (!redis.hasKey(LiquiPrefixs.LIQUI_ORDER_BOOK)) {
            return null;
        }
        if (side.equals(OrderSideEnum.BUY)) {
            if (orderbook.bids.length() > 0) {
                JSONArray arr = orderbook.bids.getJSONArray(0);
                return BigDecimal.valueOf(arr.getDouble(0));
            }
            LOGGER.warn("未能读取买盘订单薄列表: bids={}", orderbook.bids.length());
            return null;
        } 
        if (orderbook.asks.length() > 0) {
            JSONArray arr = orderbook.asks.getJSONArray(0);
            return BigDecimal.valueOf(arr.getDouble(0));
        }
        LOGGER.warn("未能读取卖盘订单薄列表: bids={}", orderbook.bids.length());
        return null;
    }
    
    // 读取实时 orderbook
    public OrderBook readOrderBookRealTime() {
        if (!redis.hasKey(LiquiPrefixs.LIQUI_ORDER_BOOK)) {
            return null;
        }
        String orderBookString = redis.get(LiquiPrefixs.LIQUI_ORDER_BOOK).toString();
        OrderBook orderbook = HttpAssets.parseJson(orderBookString, new TypeReference<OrderBook>() {});
        return orderbook;
    }
    
}
