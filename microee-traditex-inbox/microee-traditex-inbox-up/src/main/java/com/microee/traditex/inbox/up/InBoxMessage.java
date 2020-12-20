package com.microee.traditex.inbox.up;

import org.json.JSONObject;
import com.microee.traditex.inbox.oem.constrants.VENDER;

//time0 源头时间
//timeA 收到时间
//timeB 处理时间
//timeC 入库时间
//timeD 广播时间
public class InBoxMessage {

    public static final String SHUTDOWN = "shutdown";
    public static final String ERROR = "error";
    public static final String QUOTE = "quote"; // 报价等同于 orderbook
    public static final String CONNECTED = "connected"; // 连接成功打开
    public static final String DISCONNECTED = "disconnected"; // 连接已断开
    public static final String TIMEOUT = "timeout"; // 连接超时
    public static final String PRICE_STREAM = "pricing-stream";
    public static final String PING = "ping";
    public static final String FAILED = "failed";
    public static final String SUBBED = "subbed";
    public static final String UNSUB = "unsub";
    public static final String UNSUBBED = "unsubbed";
    public static final String ACCOUNT_CHANGE = "account-change";
    public static final String ORDER_STATS = "order-stats";
    public static final String UNKNOW = "unknow";
    public static final String AUTH = "auth";

    public static JSONObject getMessage(
            String connid, VENDER vender, String event, JSONObject message, JSONObject times) {
        JSONObject _message = new JSONObject();
        _message.put("connid", connid);
        if (vender != null) {
            _message.put("vender", vender.name());
        }
        _message.put("event", event);
        _message.put("_times", times);
        _message.put("message", message);
        return _message;
    }

    public static JSONObject getMessage(
            String connid, VENDER vender, String event, JSONObject message, JSONObject config, JSONObject times) {
        JSONObject _message = new JSONObject();
        _message.put("connid", connid);
        if (vender != null) {
            _message.put("vender", vender.name());
        }
        _message.put("event", event);
        _message.put("_times", times);
        _message.put("_config", config);
        _message.put("message", message);
        return _message;
    }
        
}
