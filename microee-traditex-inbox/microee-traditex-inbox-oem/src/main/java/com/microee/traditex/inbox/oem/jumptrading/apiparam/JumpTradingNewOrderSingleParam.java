package com.microee.traditex.inbox.oem.jumptrading.apiparam;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

// will replaced NewOrderSingleReq
public class JumpTradingNewOrderSingleParam implements Serializable {

    private static final long serialVersionUID = -2824789893113846595L;

    /**
     * Jump Trading支持的交易对
     */
    @JsonProperty("Symbol")
    private String symbol;

    /**
     * 由客户端指定的订单唯一标识符(unique order identifier). ClOrdID不能超过32个字符。
     */
    @JsonProperty("ClOrdID")
    private String clOrdID;

    /**
     * 订单类型, “1”:市价(Market) “2”: 限价(limit)。市价单的情况下，需要指定limit price(最低价格)。 (暂且只支持Limit Order 这一种形式)
     */
    @JsonProperty("OrdType")
    private String ordType;

    /**
     * 可接受数值为 "0, 1, 2, 3, 4"。"0": 当天有效, "1": 取消之前都是有效, "2": 接近开盘价成交，开盘后没有成交则取消, "3":
     * 没有成交或者部分没有成交则取消, "4": 没有成交则取消 0=day, 1=GTC, 3=IOC, 4=FOK
     */
    @JsonProperty("TimeInForce")
    private String timeInForce;

    /**
     * 指定交易方 "1": 买方，“2”: 卖方
     */
    @JsonProperty("Side")
    private String side;

    /**
     * 订单价格 单位为小数
     */
    @JsonProperty("Price")
    private String price;

    /**
     * 订单数量，Jump Trading不支持分数形式。
     */
    @JsonProperty("OrderQty")
    private String orderQty;

    /**
     * 在指定的时间内(秒)，没有交易订单自动取消。如果在连接不稳定的情况，这是一个可选的安全机制。0为使该功能无效。
     */
    @JsonProperty("Managed")
    private Integer managed;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getClOrdID() {
        return clOrdID;
    }

    public void setClOrdID(String clOrdID) {
        this.clOrdID = clOrdID;
    }

    public String getOrdType() {
        return ordType;
    }

    public void setOrdType(String ordType) {
        this.ordType = ordType;
    }

    public String getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(String orderQty) {
        this.orderQty = orderQty;
    }

    public Integer getManaged() {
        return managed;
    }

    public void setManaged(Integer managed) {
        this.managed = managed;
    }
    
}
