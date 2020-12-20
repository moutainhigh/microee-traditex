package com.microee.traditex.liqui.app.components;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.liqui.oem.OrderBook;

@Component
public class OrderBookAdapter {
    
    public OrderBook adapter(String orderBookId, String connid, VENDER vender, JSONObject orderbook) {
        if (vender.equals(VENDER.HBiTex)) {
            String symbol = orderbook.getString("ch").split("\\.")[1];
            JSONArray asks = orderbook.getJSONObject("tick").getJSONArray("asks");
            JSONArray bids = orderbook.getJSONObject("tick").getJSONArray("bids");
            return new OrderBook(orderBookId, symbol, asks, bids);
        }
        return null;
    }
    
}
