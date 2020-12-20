package com.microee.traditex.inbox.up;

import org.json.JSONObject;

/**
 * 消息监听
 * @author keesh
 *
 */
public interface CombineMessageListener {
    
    /**
     * 建立连接成功
     * @param vender
     * @param memo
     */
    public void onConnected(String vender, String connid, JSONObject message);

    /**
     * 连接已断开
     * @param vender
     * @param memo
     */
    public void onDisconnected(String vender, String connid, JSONObject message, Long receiveTime);
    
    /**
     * 收到远程服务器ping
     * @param vender
     * @param connid
     * @param jsonObject
     * @param receiveTime
     */
    public void onPingMessage(String vender, String connid, JSONObject message, Long receiveTime);
    
    /**
     * 收到盘口深度推送
     * @param vender
     * @param message
     */
    public void onOrderBookMessage(String vender, String connid, JSONObject message, Long time0);
    
    /**
     * 连接失败
     * @param vender
     * @param connid
     * @param message
     * @param eventTime 发生时间
     */
    public void onFailed(String vender, String connid, JSONObject message, Long eventTime);

    /**
     * 连接超时
     * @param vender
     * @param connid
     * @param message
     * @param eventTime 发生时间
     * @param start 开始时间
     * @param start 结束时间
     */
    public void onTimeout(String vender, String connid, JSONObject message, Long eventTime, Long start, Long end);

    /**
     * 订阅成功
     * @param vender
     * @param string
     */
    public void onSubbedMessage(String vender, String connid, JSONObject message, String topic, Long receiveTime);
    
    /**
     * 取消订阅成功
     * @param vender
     * @param connid
     * @param message
     * @param topic
     */
    public void onUnSubbedMessage(String vender, String connid, JSONObject message, String topic);

    /**
     * 错误消息推送
     * @param vender
     * @param string
     */
    public void onErrorMessage(String vender, String connid, JSONObject message, Long receiveTime);

    /**
     * 账户信息推送
     * @param vender
     * @param connid
     * @param message
     * @param receiveTime
     */
    public void onAccountMessage(String vender, String connid, JSONObject message, Long receiveTime);

    // 订单状态变化
    public void onOrderStatMessage(String vender, String connid, JSONObject jsonObject, Long receiveTime);

    // 外汇期货市场价格变动
    public void onPricingSteamMessage(String vender, String connid, JSONObject message, Long receiveTime);

    // 鉴权成功
    public void onAuthMessage(String vender, String connid, JSONObject jsonObject, Long receiveTime);
    
}
