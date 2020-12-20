package com.microee.traditex.liqui.app.core.disk;

import java.io.Serializable;
import java.math.BigDecimal;
import org.json.JSONArray;
import org.json.JSONObject;
import com.microee.traditex.liqui.oem.models.LiquiRiskStrategySettings;


public class DiskOrderParam implements Serializable {

    private static final long serialVersionUID = 1L;

    public String symbol;
    public String side;
    public String orderType;
    public String amount;
    public String price;
    public String clientOrderId;

    public DiskOrderParam() {

    }

    public DiskOrderParam(String symbol, String side, String orderType,
            String amountString, String priceString, String clientOrderId) {
        super();
        this.symbol = symbol;
        this.side = side;
        this.orderType = orderType;
        this.amount = amountString;
        this.price = priceString;
        this.clientOrderId = clientOrderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    // 取得价格比例参数设置
    public static PriceAndQuantityIncrRate getDiskOrderIncrRate(String orderType, int pick, LiquiRiskStrategySettings settings) {
        if (settings == null) {
            return null;
        }
        JSONArray jsonArray = null;
        if (orderType.indexOf("buy") != -1) {
            String buyPriceIncrRateJson = settings.getBuyPriceIncrRate();
            if (buyPriceIncrRateJson == null || buyPriceIncrRateJson.trim().isEmpty()) {
                return null;
            }
            jsonArray = new JSONArray(buyPriceIncrRateJson);
        }

        if (orderType.indexOf("sell") != -1){
            String sellPriceIncrRateJson = settings.getSellPriceIncrRate();
            if (sellPriceIncrRateJson == null || sellPriceIncrRateJson.trim().isEmpty()) {
                return null;
            }
            jsonArray = new JSONArray(sellPriceIncrRateJson);
        }
        if (jsonArray == null || jsonArray.length() <= 0) {
            return null;
        }

        // target先等于一个公用的
        // idx为0的是公用的, 从1开始是定制的
        // 如果没获取到就用公共的
        // 应为配置的策略有可能比拉取的档位数据小
        JSONObject target = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject current = jsonArray.getJSONObject(i);
            boolean getCur = current.has("id") && (0 == current.getInt("id") || (pick + 1) == current.getInt("id"));
            if (getCur) {
                target = current;
            }
        }

        if (null == target) {
            return null;
        }

        BigDecimal price = target.has("price") ? new BigDecimal(target.getString("price")) : null;
        BigDecimal quantity = target.has("quantity") ? new BigDecimal(target.getString("quantity")) : null;
        if (price == null || quantity == null) {
            return null;
        }

        return new PriceAndQuantityIncrRate(price, quantity);
    }

    public static class PriceAndQuantityIncrRate {

        public BigDecimal priceIncr;
        public BigDecimal quantityIncr;

        public PriceAndQuantityIncrRate() {
            super();
            this.priceIncr = BigDecimal.ZERO;
            this.quantityIncr = BigDecimal.ZERO;
        }

        public PriceAndQuantityIncrRate(BigDecimal price, BigDecimal quantity) {
            super();
            this.priceIncr = price;
            this.quantityIncr = quantity;
        }

        public BigDecimal getPriceIncr() {
            return priceIncr;
        }

        public void setPriceIncr(BigDecimal priceIncr) {
            this.priceIncr = priceIncr;
        }

        public BigDecimal getQuantityIncr() {
            return quantityIncr;
        }

        public void setQuantityIncr(BigDecimal quantityIncr) {
            this.quantityIncr = quantityIncr;
        }

    }

}
