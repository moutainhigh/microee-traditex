package com.microee.traditex.liqui.app.consumer;

import javax.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.response.R;
import com.microee.stacks.kafka.consumer.KafkaSubscribe;
import com.microee.traditex.inbox.rmi.TradiTexConnectorClient;
import com.microee.traditex.liqui.app.components.AccountBalanceService;
import com.microee.traditex.liqui.app.components.LiquiRiskSettings;
import com.microee.traditex.liqui.app.components.OrderStatService;
import com.microee.traditex.liqui.app.components.PricingStreamService;
import com.microee.traditex.liqui.app.core.conn.ConnectBootstrap;
import com.microee.traditex.liqui.app.core.conn.ConnectServiceMap;
import com.microee.traditex.liqui.app.core.disk.DiskTableService;
import com.microee.traditex.liqui.app.props.conf.HBiTexAccountConf;
import com.microee.traditex.liqui.oem.enums.LiquiAccountType;
import com.microee.traditex.liqui.oem.models.LiquiRiskStrategySettings;

@Component
public class TradiTexKafkaComsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradiTexKafkaComsumer.class);
    
    @Autowired
    private KafkaSubscribe kafkaSubscribe;

    @Value("${topics.inbox.orderbook}")
    private String orderBookTopic;
    
    @Value("${topics.inbox.connected}")
    private String connectedTopic;
    
    @Value("${topics.inbox.pricing}")
    private String pricingTopic;
    
    @Value("${topics.inbox.banalce}")
    private String banalceTopic;
    
    @Value("${topics.inbox.orderstat}")
    private String orderStatTopic;
    
    @Value("${topics.inbox.authevent}")
    private String authEventTopic;
    
    @Value("${topics.inbox.subscribe.event}")
    private String subscribeEventTopic;
    
    @Value("${topics.inbox.connect.shutdown.event}")
    private String connectShutDownEventTopic;

    @Autowired
    private ConnectBootstrap connectService;

    @Autowired
    private ConnectServiceMap connectServiceMap;
    
    @Autowired
    private DiskTableService diskService;

    @Autowired
    private PricingStreamService pricingStreamService;

    @Autowired
    private OrderStatService orderStatChangeService;

    @Autowired
    private AccountBalanceService accountStatChangeService;

    @Autowired
    private TradiTexConnectorClient tradiTexConnectorClient;

    @Autowired
    private ConnectBootstrap setupConnectService;

    @Autowired
    private LiquiRiskSettings liquiConf;
    
    @PostConstruct
    public void init() {
        kafkaSubscribe.create(this::onOrderBookMessageConsumer, orderBookTopic).start();
        kafkaSubscribe.create(this::onConnectedSuccessConsumer, connectedTopic).start();
        kafkaSubscribe.create(this::onPricingStreamConsumer, pricingTopic).start();
        kafkaSubscribe.create(this::onAccountBalanceChangeConsumer, banalceTopic).start();
        kafkaSubscribe.create(this::onOrderStatChangeConsumer, orderStatTopic).start();
        kafkaSubscribe.create(this::onAuthEventConsumer, authEventTopic).start();
        kafkaSubscribe.create(this::onSubscribeEventConsumer, subscribeEventTopic).start();
        kafkaSubscribe.create(this::onConnectShutDownEventConsumer, connectShutDownEventTopic).start();
    }
    
    public void onConnectShutDownEventConsumer(ConsumerRecord<String, String> record) {
        String topic = record.topic();
        String messageId = record.key();
        String message = record.value();
        LOGGER.info("onConnectShutDownEventConsumer: topic={}, messageId={}, message={}", topic, messageId, message);
        // 延迟队列启动重连
        // TODO
    }
    
    public void onSubscribeEventConsumer(ConsumerRecord<String, String> record) {
        String topic = record.topic();
        String messageId = record.key();
        String message = record.value();
        LOGGER.info("onSubscribeEventConsumer: topic={}, messageId={}, message={}", topic, messageId, message);
    }
    
    public void onConnectedSuccessConsumer(ConsumerRecord<String, String> record) {
        String message = record.value();
        JSONObject theMessage = new JSONObject(message);
        String connid = theMessage.getString("connid");
        String connidSolr = setupConnectService.getConnidSolrConnid();
        String connidDisk = setupConnectService.getConnidDiskConnid();
        String connectPrimary = theMessage.getJSONObject("message").getString("connectPrimary"); 
        if (connectPrimary.equals("orderbook")) {
            String _symbol = "btcusdt";
            LiquiRiskStrategySettings settings = liquiConf.getCachedStrategySettings(_symbol);
            if (settings == null) {
                LOGGER.error("没有配置流动性网络延迟报警");
                return;
            }
            String _step = "step" + settings.getDepthStep();
            LOGGER.info("盘口订单薄连接建立成功, 即将发起订阅: symbol={}, step={} topic={}, messageId={}", _symbol, _step, record.topic(), record.key());
            tradiTexConnectorClient.hbitexOrderBookSub(connid, _step, _symbol);
            return;
        }
        if (connectPrimary.equals("orderbalance")) {
            String uid = theMessage.getJSONObject("message").getString("uid");
            HBiTexAccountConf _account = this.connectServiceMap.getAccountByUid(uid);
            if (_account == null) {
                LOGGER.error("资产及订单状态变化连接建立成功, 收到无效的uid, 无法鉴权: uid={}, messageId={}", uid, record.key());
                return;
            }
            if (connidDisk != null && connidDisk.equals(connid)) {
                LOGGER.info("挂单资产及订单状态变化连接建立成功, 即将发起用户鉴权: uid={}, messageId={}", uid, record.key());
            }
            if (connidSolr != null && connidSolr.equals(connid)) {
                LOGGER.info("甩单资产及订单状态变化连接建立成功, 即将发起用户鉴权: uid={}, messageId={}", uid, record.key());
            }
            tradiTexConnectorClient.hbitexOrderBalanceLogin(connid, uid, _account.getSpotId(), _account.getAccessKey(), _account.getSecretKey());
            return;
        }
        LOGGER.info("onConnectedSuccessConsumer: topic={}, messageId={}, message={}", record.topic(), record.key(), message);
    }
    
    public void onOrderBookMessageConsumer(ConsumerRecord<String, String> record) {
        String message = record.value();
        JSONObject theMessage = new JSONObject(message);
        String connid = theMessage.getString("connid");
        if (connid.equals(connectServiceMap.getConnidByType("orderbook")))  {
            diskService.process(theMessage);
        }
    }

    public void onPricingStreamConsumer(ConsumerRecord<String, String> record) {
        pricingStreamService.storePrice(new JSONObject(record.value()));
    }

    public void onAccountBalanceChangeConsumer(ConsumerRecord<String, String> record) {
        JSONObject accountBalance = new JSONObject(record.value());
        String connid = accountBalance.getString("connid");
        if (connid.equals(connectServiceMap.getConnidByType("diskorder")) || connid.equals(connectServiceMap.getConnidByType("solrorder"))) {
            accountStatChangeService.process(accountBalance);
        }
    }

    public void onOrderStatChangeConsumer(ConsumerRecord<String, String> record) {
        JSONObject orderstate = new JSONObject(record.value());
        String connid = orderstate.getString("connid");
        if (connid.equals(connectServiceMap.getConnidByType("diskorder")) || connid.equals(connectServiceMap.getConnidByType("solrorder"))){
            orderStatChangeService.process(orderstate);
        }
    }
    
    @Autowired
    private AccountBalanceService balanceService;

    public void onAuthEventConsumer(ConsumerRecord<String, String> record) {
        String message = record.value();
        JSONObject theMessage = new JSONObject(message);
        String connid = theMessage.getString("connid");
        String vender = theMessage.getString("vender");
        connectService.addedAuthedConnId(connid, vender);
        R<Boolean> subscribeResult = tradiTexConnectorClient.hbitexAccountSubscribe(connid); // 订阅账户状态变化
        LOGGER.warn("订阅账户状态变化结果: connnid={}, messageId={}", connid, HttpAssets.toJsonString(subscribeResult));
        String connidSolr = setupConnectService.getConnidSolrConnid();
        String connidDisk = setupConnectService.getConnidDiskConnid();
        if (connidDisk != null && connidDisk.equals(connid)) {
            LOGGER.info("挂单用户鉴权成功, 即将发起资产及订单状态变化订阅: connnid={}, symbol=btcjpy, messageId={}, theMessage={}", connid, record.key(), message);
            tradiTexConnectorClient.hbitexOrderSubscribe(connid, "btcjpy"); // 订阅订单状态变化
            balanceService.readAccountBalance(LiquiAccountType.DISK, "jpy", true);
            balanceService.readAccountBalance(LiquiAccountType.DISK, "btc", true);
            return;
        }
        if (connidSolr != null && connidSolr.equals(connid)) {
            LOGGER.info("甩单用户鉴权成功, 即将发起资产及订单状态变化订阅: connnid={}, symbol=btcusdt, messageId={}, theMessage={}", connid, record.key(), message);
            tradiTexConnectorClient.hbitexOrderSubscribe(connid, "btcusdt"); // 订阅订单状态变化
            balanceService.readAccountBalance(LiquiAccountType.SOLR, "usdt", true);
            balanceService.readAccountBalance(LiquiAccountType.SOLR, "btc", true);
            return;
        }
    }
    
}
