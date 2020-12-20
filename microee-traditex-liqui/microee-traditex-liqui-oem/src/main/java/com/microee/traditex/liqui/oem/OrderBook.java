package com.microee.traditex.liqui.oem;

import java.io.Serializable;
import org.json.JSONArray;

public class OrderBook implements Serializable {

    private static final long serialVersionUID = 1167256708349921393L;
    
    public String orderBookId; 
    public String symbol;
    public JSONArray asks;
    public JSONArray bids;
    
    public OrderBook() {
        
    }
    
    public OrderBook(String orderBookId, String symbol, JSONArray asks, JSONArray bids) {
        this.orderBookId = orderBookId;
        this.symbol = symbol;
        this.bids = bids;
        this.asks = asks;
    }
    
}
