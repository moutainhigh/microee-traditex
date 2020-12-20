package com.microee.traditex.inbox.up.jumptrading;

import java.time.Instant;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.microee.traditex.inbox.oem.constrants.ConnectStatus;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.inbox.oem.jumptrading.apiresult.JumpTradingApiResultBase;
import com.microee.traditex.inbox.up.CombineMessageListener;
import com.microee.traditex.inbox.up.InBoxMessage;
import okhttp3.Request;

// orderbook
//@formatter:off
//{
//    "0": {
//        ".00865": {
//            "size": "20000.00000000"
//        },
//        ".00856": {
//            "size": "30000.00000000"
//        },
//        ".00874": {
//            "size": "10000.00000000"
//        }
//    },
//    "BeginString": "REST.2.0",
//    "1": {
//        ".00901": {
//            "size": "30000.00000000"
//        },
//        ".00892": {
//            "size": "20000.00000000"
//        },
//        ".00883": {
//            "size": "10000.00000000"
//        }
//    },
//    "SendingTime": "20201105-12:28:47.2401352",
//    "QuoteID": null,
//    "Symbol": "LTCBTC",
//    "MsgType": "REST_BOOKSYNCH",
//    "FIXSeq": 102582056,
//    "MsgSeqNum": 102582056,
//    "seq": 3454440,
//    "timestamp": 1.604579327187E9,
//    "clientTS": "20201105-12:28:47.1881583"
//}
//@formatter:on

public class OrderBookStreamHandler {

    private static final Logger logger = LoggerFactory.getLogger(OrderBookStreamHandler.class);
    public static final VENDER _VENDER = VENDER.JumpTrading;

    private String connid;
    private ConnectStatus connectStatus;
    private final CombineMessageListener combineMessageListener;

    public OrderBookStreamHandler(final String connid, final CombineMessageListener combineMessageListener) {
        this.connid = connid;
        this.connectStatus = ConnectStatus.UNKNOW;
        this.combineMessageListener = combineMessageListener;
    }

    public ConnectStatus getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(ConnectStatus connectStatus) {
        this.connectStatus = connectStatus;
    }

    public void onStreamMessage(Request request, String line) {
        Long receiveTime = Instant.now().toEpochMilli();
        JSONObject _times = new JSONObject();
        _times.put("timeA", receiveTime); // 收到时间
        JSONObject jsonObject = new JSONObject(line);
        JumpTradingApiResultBase lineObject = JumpTradingApiResultBase.parseResult(line);
        if (lineObject.getMsgType().equals("REST_BOOKSYNCH")) {
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onOrderBookMessage(_VENDER.name(), connid, 
                    InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.QUOTE, jsonObject, _times), Instant.now().toEpochMilli());
            return;
        }
        if (lineObject.getMsgType().equals("A")) {
            logger.warn("jumptrading连接建立成功, vender={}, url={}, message={}", _VENDER.name(), request.url(), line);
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onConnected(_VENDER.name(), connid, InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.CONNECTED, jsonObject, _times));
            return;
        }
        if (lineObject.getMsgType().equals("5")) {
            this.setConnectStatus(ConnectStatus.DAMAGED);
            // {"BeginString": "REST.2.0", "MsgType": "5", "MsgSeqNum": 0, "SendingTime": "20201105-16:07:08.151368", "Text": "User logoff"}
            logger.warn("jumptrading退出登录, vender={}, url={}, message={}", _VENDER.name(), request.url(), line);
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onDisconnected(_VENDER.name(), connid, InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.DISCONNECTED, jsonObject, _times), Instant.now().toEpochMilli());
            return;
        }
        if (lineObject.getMsgType().equals("8")) {
            logger.warn("收到订单状态变化`Status: '0' (New), '1' (PartialFill'), '2' (Filled), '3' (DoneForDay), '4' (Cancelled) or '8' (Rejected)`, vender={}, message={}", _VENDER.name(), line);
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onOrderStatMessage(_VENDER.name(), connid, InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.DISCONNECTED, jsonObject, _times), Instant.now().toEpochMilli());
            return;
        }
        if (lineObject.getMsgType().equals("REST_MSG_TYPE_TEST_REQUEST_ACK")) {
            // 心跳响应
            _times.put("timeB", Instant.now().toEpochMilli()); // 处理时间
            this.combineMessageListener.onPingMessage(_VENDER.name(), connid, 
                    InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.PING, jsonObject, _times), receiveTime);
            return;
        }
        logger.warn("收到一条消息未处理, vender={}, url={}, message={}", _VENDER.name(), request.url(), line);
    }

    public void onDisconnectTrigger(Request request, Exception e) {
        Long eventTime = Instant.now().toEpochMilli();
        JSONObject _times = new JSONObject();
        _times.put("timeA", eventTime); // 收到时间
        JSONObject lineObject = new JSONObject();
        lineObject.put("errorMessage", e.getMessage());
        combineMessageListener.onFailed(_VENDER.name(), this.connid, 
                InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.FAILED, lineObject, null, _times), eventTime);
    }

    public void onOpenTrigger(Request request) {
        JSONObject _times = new JSONObject();
        _times.put("timeA", Instant.now().toEpochMilli()); // 收到时间
        JSONObject lineObject = new JSONObject();
        lineObject.put("connid", connid);
        lineObject.put("connectPrimary", "orderbook-stream"); // 一级连接类型
        lineObject.put("desc", "jumptrading-orderbook建立成功,即将开始推送");
        combineMessageListener.onConnected(_VENDER.name(), this.connid, 
                InBoxMessage.getMessage(connid, _VENDER, InBoxMessage.FAILED, lineObject, _times));
    }
    
}
