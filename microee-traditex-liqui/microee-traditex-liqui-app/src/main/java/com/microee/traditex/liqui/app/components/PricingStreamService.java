package com.microee.traditex.liqui.app.components;

import java.math.BigDecimal;
import java.time.Instant;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.microee.stacks.redis.support.Redis;
import com.microee.stacks.redis.support.RedisHash;
import com.microee.traditex.inbox.oem.constrants.OrderSideEnum;
import com.microee.traditex.liqui.app.constrants.LiquiPrefixs;
import com.microee.traditex.liqui.app.producer.RedisProducer;

// 外汇价格缓存
@Component
public class PricingStreamService {

    @Autowired
    private RedisHash redisHash;
    
    @Autowired
    private Redis redis;

    @Autowired
    private RedisProducer redisProducer;

    // @formatter:off
    //    {
    //        "_times": {
    //            "timeA": 1600077316742,
    //            "timeB": 1600077316742
    //        },
    //        "connid": "Ar8bAomLvE",
    //        "vender": "Oanda",
    //        "event": "pricing-stream",
    //        "message": {
    //            "closeoutBid": "106.027",
    //            "closeoutAsk": "106.040",
    //            "asks": [
    //                {
    //                    "price": "106.036",
    //                    "liquidity": 250000
    //                }
    //            ],
    //            "bids": [
    //                {
    //                    "price": "106.032",
    //                    "liquidity": 250000
    //                }
    //            ],
    //            "tradeable": true,
    //            "instrument": "USD_JPY",
    //            "time": "1600077316.505030971",
    //            "type": "PRICE",
    //            "status": "tradeable"
    //        }
    //    }
    // @formatter:on
    public void storePrice(JSONObject message) {
        String connid = message.getString("connid");
        JSONObject messageObject = message.getJSONObject("message");
        JSONArray bidsArray = messageObject.getJSONArray("bids");
        JSONArray asksArray = messageObject.getJSONArray("asks");
        String time = messageObject.getString("time");
        String instrument =
                messageObject.getString("instrument").replaceAll("[^a-zA-Z0-9]+", "").toUpperCase(); // 剔除特殊字符并转大写
        String bidPrice = bidsArray == null || bidsArray.length() == 0 ? null : bidsArray.getJSONObject(0).getString("price");
        String askPrice = asksArray == null || asksArray.length() == 0 ? null : asksArray.getJSONObject(0).getString("price");
        if (bidPrice == null || askPrice == null) {
            return;
        }
        String pricingKey = LiquiPrefixs.PRICING_STREAM_PREFIX + instrument; // __liqui_price-stream:USDJPY
        redisHash.hset(pricingKey, OrderSideEnum.BUY.code, bidPrice);
        redisHash.hset(pricingKey, OrderSideEnum.SELL.code, askPrice);
        redisProducer.broadcasePricing(new JSONObject().put("buy", bidPrice).put("sell", askPrice).put("instrument", instrument).put("time", time).put("connid", connid));
    }

    public BigDecimal readPrice(OrderSideEnum side, String instrument) {
        String pricingKey = LiquiPrefixs.PRICING_STREAM_PREFIX + instrument;
        if (!redis.hasKey(pricingKey)) {
            return null;
        }
        if (!redisHash.hHasKey(pricingKey, side.code)) {
            return null;
        }
        return new BigDecimal(redisHash.hget(pricingKey, side.code).toString());
    }
    
    public JSONArray getPricing() {
        BigDecimal bidPrice = this.readPrice(OrderSideEnum.BUY, "USDJPY");
        BigDecimal askPrice = this.readPrice(OrderSideEnum.SELL, "USDJPY");
        String bidPriceString = bidPrice == null ? null : bidPrice.toPlainString();
        String askPriceString = askPrice == null ? null : askPrice.toPlainString();
        JSONArray arr = new JSONArray();
        arr.put(new JSONObject().put("buy", bidPriceString).put("sell", askPriceString).put("instrument", "USDJPY").put("time", Instant.now().toEpochMilli()));
        return arr;
    }

}
