package com.microee.traditex.liqui.app.producer;

import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.stacks.redis.support.RedisMessage;
import com.microee.traditex.inbox.oem.hbitex.vo.AccountBalanceList;
import com.microee.traditex.liqui.app.caches.LiquiCachesComponent;
import com.microee.traditex.liqui.oem.OrderBook;
import com.microee.traditex.liqui.oem.enums.LiquiAccountType;

@Component
public class RedisProducer {

    @Value("${topic.traditex.pricing.message}")
    private String tradiTexPricingTopic;

    @Value("${topic.traditex.orderbook.message}")
    private String tradiTexOrderBookMessageTopic;

    @Value("${topic.traditex.diskorders.message}")
    private String tradiTexDiskOrdersMessageTopic;

    @Value("${topic.traditex.revokeorder.count.message}")
    private String tradiTexRevokeOrderCountMessageTopic;

    @Value("${topic.traditex.account.balance.message}")
    private String tradiTexAccountBalanceMessageTopic;

    @Value("${topic.traditex.account-disk.balancess.message}")
    private String tradiTexAccountDiskBalancessMessageTopic;

    @Value("${topic.traditex.account-solr.balancess.message}")
    private String tradiTexAccountSolrBalancessMessageTopic;

    @Value("${topic.traditex.httpnetwork.message}")
    private String tradiTexHttpNetworkMessageTopic;

    @Autowired
    private RedisMessage redisMessage;

    @Autowired
    private LiquiCachesComponent liquiCaches;

    public void broadcaseOrderBook(OrderBook orderbook) {
        redisMessage.send(tradiTexOrderBookMessageTopic, liquiCaches.cacheOrderbook(orderbook));
    }

    public void broadcasePricing(JSONObject pricing) {
        redisMessage.send(tradiTexPricingTopic, pricing.toString());
    }

    public void broadcaseDiskOrders(Map<Object, Object> pendingOrders) {
        redisMessage.send(tradiTexDiskOrdersMessageTopic, HttpAssets.toJsonString(pendingOrders));
    }

    public void broadcaseHttpNetwork(JSONObject httpMessage) {
        redisMessage.send(tradiTexHttpNetworkMessageTopic, httpMessage.toString());
    }

    public void broadcaseRevokeOrderCount(Map<Object, Double> revokeCountScore) {
        redisMessage.send(tradiTexRevokeOrderCountMessageTopic,
                HttpAssets.toJsonString(revokeCountScore));
    }

    public void broadcaseAccountBalance(JSONObject message) {
        redisMessage.send(tradiTexAccountBalanceMessageTopic, message.toString());
    }

    public void broadcaseAccountBalancess(LiquiAccountType accountType, List<AccountBalanceList> balance) {
        if (accountType.equals(LiquiAccountType.DISK)) {
            redisMessage.send(tradiTexAccountDiskBalancessMessageTopic, HttpAssets.toJsonString(balance));
        }
        if (accountType.equals(LiquiAccountType.SOLR)) {
            redisMessage.send(tradiTexAccountSolrBalancessMessageTopic, HttpAssets.toJsonString(balance));
        }
    }
    
}
