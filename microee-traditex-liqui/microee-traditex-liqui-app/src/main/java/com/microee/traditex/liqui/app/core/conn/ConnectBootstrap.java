package com.microee.traditex.liqui.app.core.conn;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.response.R;
import com.microee.plugin.thread.ThreadPoolFactoryLow;
import com.microee.traditex.inbox.rmi.TradiTexConnectorClient;
import com.microee.traditex.liqui.app.props.LiquisConfProps;
import com.microee.traditex.liqui.app.props.ProxyProps;
import com.microee.traditex.liqui.app.props.conf.Conf;
import com.microee.traditex.liqui.app.props.conf.HBiTexAccountConf;
import com.microee.traditex.liqui.app.props.conf.OandaStreamConf;
import com.microee.traditex.liqui.app.props.conf.OrderBookHBiTexConf;

@Component
public class ConnectBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectBootstrap.class);

    public static final int DELAY_CONNECT_TIME_SEC = 8; // 8 秒后建立全部连接
    private final DelayQueue<DelayedConnectItem<ConnectBootstrap>> connectDealyQueue =
            new DelayQueue<>();
    private static ThreadPoolFactoryLow threadPool =
            ThreadPoolFactoryLow.newInstance("traditex-liqui-建立连接延迟队列线程池");

    @Autowired
    private ConnectServiceMap connectServiceMap;

    @Autowired
    private ProxyProps proxyProps;

    @Autowired
    private LiquisConfProps liquisConfProps;

    @Autowired
    private TradiTexConnectorClient tradiTexConnectorClient;

    @PostConstruct
    public void init() {
        this.setup();
    }

    // 将连接对象放入延迟队列等待重连
    public ConnectBootstrap add(ConnectBootstrap connection) {
        connectDealyQueue.put(
                new DelayedConnectItem<>(connection, DELAY_CONNECT_TIME_SEC, TimeUnit.SECONDS));
        return this;
    }

    public void setup() {
        threadPool.pool().submit(() -> {
            while (true) {
                DelayedConnectItem<ConnectBootstrap> connect = null;
                try {
                    connect = connectDealyQueue.take();
                } catch (InterruptedException e) {
                    LOGGER.error("建立长连接异常: delayQueueSize={}, erroeMessage={}",
                            connectDealyQueue.size(), e.getMessage(), e);
                    return;
                }
                for (int i = 0; i < liquisConfProps.getConfs().size(); i++) {
                    Conf orderBookConf = liquisConfProps.getConfs().get(i).getOrderbook();
                    if (orderBookConf.getAutoconnect()) {
                        connect.item.setupOrderBookConnect(orderBookConf);
                    }
                    Conf hdegConf = liquisConfProps.getConfs().get(i).getHedging();
                    if (hdegConf.getAutoconnect()) {
                        connect.item.setupOandaConnect(hdegConf, new String[] {"USD_JPY"});
                    }
                    Conf diskConf = liquisConfProps.getConfs().get(i).getDisk();
                    if (diskConf.getAutoconnect()) {
                        connect.item.setupDiskAccountConnect(diskConf);
                    }
                    Conf solrConf = liquisConfProps.getConfs().get(i).getSolr();
                    if (solrConf.getAutoconnect()) {
                        connect.item.setupSolrAccountConnect(solrConf);
                    }
                }
            }
        });
    }

    // 创建连接ID
    public String createConnectId() {
        return tradiTexConnectorClient.genid().getData();
    }

    // 获取摆盘长连接id
    public String getConnidDiskConnid() {
        String connid = this.connectServiceMap.getConnidByType("diskorder");
        if (connid == null) {
            return null;
        }
        return connid;
    }

    // 获取甩单长连接id
    public String getConnidSolrConnid() {
        String connid = this.connectServiceMap.getConnidByType("solrorder");
        if (connid == null) {
            return null;
        }
        return connid;
    }

    // 获取甩单长连接id
    public String getConnidHdegConnid() {
        String connid = this.connectServiceMap.getConnidByType("oanda");
        if (connid == null) {
            return null;
        }
        return connid;
    }

    // 获取 orderbook 长连接id
    public String getOrderBookConnid() {
        String connid = this.connectServiceMap.getConnidByType("orderbook");
        if (connid == null) {
            return null;
        }
        return connid;
    }

    // 获取已经通过验证的长连接id
    public String getAuthedConnidConnid(String connid) {
        String vender = this.connectServiceMap.gettAuthIdAndVender(connid);
        if (vender == null) {
            return null;
        }
        return vender;
    }

    public void addedAuthedConnId(String connid, String vender) {
        this.connectServiceMap.putAuthIdAndVender(connid, vender);
    }

    // orderbook 连接建立, 用于获取盘口深度订单列表
    public String setupOrderBookConnect(Conf conf) {
        String connid = createConnectId();
        String theProxyServer = null;
        Integer theProxyPort = null;
        OrderBookHBiTexConf orderBookHBiTexConf =
                conf.map(new TypeReference<OrderBookHBiTexConf>() {});
        if (conf.getProxyEnable()) {
            theProxyServer = proxyProps.getServer();
            theProxyPort = proxyProps.getPort();
        }
        tradiTexConnectorClient.hbitexOrderBookNew(connid, orderBookHBiTexConf.getWshost(),
                orderBookHBiTexConf.getExchangeCode(), theProxyServer, theProxyPort);
        this.connectServiceMap.putTypeAndId("orderbook", connid);
        return connid;
    }

    // 外汇期货市场建立连接, 用于获取实时外汇价格
    public String setupOandaConnect(Conf conf, String[] instruments) {
        String connid = createConnectId();
        String theProxyServer = null;
        Integer theProxyPort = null;
        OandaStreamConf oandaStreamConf = conf.map(new TypeReference<OandaStreamConf>() {});
        if (conf.getProxyEnable()) {
            theProxyServer = proxyProps.getServer();
            theProxyPort = proxyProps.getPort();
        }
        tradiTexConnectorClient.oandaPricingStreamSetup(oandaStreamConf.getStreamHost(), connid,
                oandaStreamConf.getAccountId(), oandaStreamConf.getAccessToken(), instruments,
                theProxyServer, theProxyPort);
        this.connectServiceMap.putConnIdAndAccount(connid, oandaStreamConf);
        this.connectServiceMap.putTypeAndId("oanda", connid);
        return connid;
    }

    // 摆盘账户建立连接, 用于监控资产即订单状态变化
    public String setupDiskAccountConnect(Conf conf) {
        String connid = createConnectId();
        String theProxyServer = null;
        Integer theProxyPort = null;
        HBiTexAccountConf hbiTexAccountConf = conf.map(new TypeReference<HBiTexAccountConf>() {});
        if (conf.getProxyEnable()) {
            theProxyServer = proxyProps.getServer();
            theProxyPort = proxyProps.getPort();
        }
        R<String> setupResult = tradiTexConnectorClient.hbitexOrderBalanceNew(connid,
                hbiTexAccountConf.getWshost(), hbiTexAccountConf.getExchangeCode(),
                hbiTexAccountConf.getUid(), theProxyServer, theProxyPort);
        LOGGER.info("setupDiskAccountConnect: diskorderConnid={}, setupResult={}", connid,
                HttpAssets.toJsonString(setupResult));
        this.connectServiceMap.putConnIdAndAccount(connid, hbiTexAccountConf);
        this.connectServiceMap.putTypeAndId("diskorder", connid);
        this.connectServiceMap.putHBiTexAccount(hbiTexAccountConf.getUid(), hbiTexAccountConf);
        return connid;
    }

    // 甩单账户建立连接, 用于监控资产即订单状态变化
    public String setupSolrAccountConnect(Conf conf) {
        String connid = createConnectId();
        String theProxyServer = null;
        Integer theProxyPort = null;
        HBiTexAccountConf hbiTexAccountConf = conf.map(new TypeReference<HBiTexAccountConf>() {});
        if (conf.getProxyEnable()) {
            theProxyServer = proxyProps.getServer();
            theProxyPort = proxyProps.getPort();
        }
        R<String> setupResult = tradiTexConnectorClient.hbitexOrderBalanceNew(connid,
                hbiTexAccountConf.getWshost(), hbiTexAccountConf.getExchangeCode(),
                hbiTexAccountConf.getUid(), theProxyServer, theProxyPort);
        LOGGER.info("setupDiskAccountConnect: diskorderConnid={}, setupResult={}", connid,
                HttpAssets.toJsonString(setupResult));
        this.connectServiceMap.putConnIdAndAccount(connid, hbiTexAccountConf);
        this.connectServiceMap.putTypeAndId("solrorder", connid);
        this.connectServiceMap.putHBiTexAccount(hbiTexAccountConf.getUid(), hbiTexAccountConf);
        return connid;
    }

}


class DelayedConnectItem<T> implements Delayed {

    private long time; /* 触发时间 */
    public T item;

    public DelayedConnectItem(T item, long time, TimeUnit unit) {
        this.item = item;
        this.time = System.currentTimeMillis() + (time > 0 ? unit.toMillis(time) : 0);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return time - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        @SuppressWarnings("rawtypes")
        DelayedConnectItem<?> item = (DelayedConnectItem) o;
        long diff = this.time - item.time;
        if (diff <= 0) {// 改成>=会造成问题
            return -1;
        }
        return 1;
    }

    @Override
    public String toString() {
        return "Item{" + "time=" + time + ", name='" + item + '\'' + '}';
    }
}

