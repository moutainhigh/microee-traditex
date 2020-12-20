package com.microee.traditex.liqui.app.components;

import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.stacks.redis.support.Redis;
import com.microee.stacks.redis.support.RedisHash;
import com.microee.traditex.inbox.oem.hbitex.vo.AccountBalanceList;
import com.microee.traditex.inbox.rmi.TradiTexHBiTexAccountClient;
import com.microee.traditex.liqui.app.components.DiskOrderMoneyLimitted.DiskAccountBalance;
import com.microee.traditex.liqui.app.constrants.LiquiPrefixs;
import com.microee.traditex.liqui.app.core.conn.ConnectBootstrap;
import com.microee.traditex.liqui.app.core.conn.ConnectServiceMap;
import com.microee.traditex.liqui.app.producer.RedisProducer;
import com.microee.traditex.liqui.app.props.conf.HBiTexAccountConf;
import com.microee.traditex.liqui.oem.enums.LiquiAccountType;

// 监控账户状态余额
@Component
public class AccountBalanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountBalanceService.class);
    
    @Autowired
    private RedisProducer redisProducer;

    @Autowired
    private RedisHash redisHash;

    @Autowired
    private ConnectBootstrap connectService;

    @Autowired
    private ConnectServiceMap connectServiceMap;

    @Autowired
    private DiskOrderMoneyLimitted diskOrderMoneyLimitted;
    
    @Autowired
    private TradiTexHBiTexAccountClient hbiTexAccountClient;

    @Autowired
    private Redis redis;
    
    public void process(JSONObject message) {
        LOGGER.info("账户资金变化: balance={}", message.toString());
        String diskBalanceKey = LiquiPrefixs.ACCOUNT_BALANCE_DISK_PREFIX;
        String solrBalanceKey = LiquiPrefixs.ACCOUNT_BALANCE_SOLR_PREFIX;
        String connid = message.getString("connid");
        HBiTexAccountConf accountConf = connectServiceMap.getAccountByConnId(connid, new TypeReference<HBiTexAccountConf>() {});
        JSONObject json = message.getJSONObject("message").getJSONObject("data");
        JSONArray list = json.getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            String currency = list.getJSONObject(i).getString("currency");
            Object balance = list.getJSONObject(i).get("balance");
            if (connectService.getConnidDiskConnid() != null
                    && connectService.getConnidDiskConnid().equals(connid)) {
                json.put("type", "disk");
                redisHash.hset(diskBalanceKey, currency, balance.toString());
                List<AccountBalanceList> accountBalanceResult = this.queryAccountBalance(LiquiAccountType.DISK);
                diskOrderMoneyLimitted.cachedAccountBalance(accountConf, LiquiAccountType.DISK, accountBalanceResult); 
                redisProducer.broadcaseAccountBalancess(LiquiAccountType.DISK, accountBalanceResult);
            }
            if (connectService.getConnidSolrConnid() != null
                    && connectService.getConnidSolrConnid().equals(connid)) {
                json.put("type", "solr");
                redisHash.hset(solrBalanceKey, currency, balance.toString());
                List<AccountBalanceList> accountBalanceResult = this.queryAccountBalance(LiquiAccountType.SOLR);
                diskOrderMoneyLimitted.cachedAccountBalance(accountConf, LiquiAccountType.SOLR, accountBalanceResult); 
                redisProducer.broadcaseAccountBalancess(LiquiAccountType.SOLR, accountBalanceResult);
            }
        }
        redisProducer.broadcaseAccountBalance(json);
    }
    
    public List<AccountBalanceList> queryAccountBalance(LiquiAccountType accountType) {
        String connid = null;
        if (accountType.equals(LiquiAccountType.DISK)) {
            connid = connectService.getConnidDiskConnid();
        } else if (accountType.equals(LiquiAccountType.SOLR)) {
            connid = connectService.getConnidSolrConnid();
        }
        if (connid == null) {
            return null;
        }
        HBiTexAccountConf accountConf = this.getAccount(accountType);
        if (accountConf == null) {
            return null;
        }
        List<AccountBalanceList> accountBalanceResult = null;
        try {
            if (accountType.equals(LiquiAccountType.DISK)) {
                accountBalanceResult = hbiTexAccountClient.balance(connid, accountConf.getDawnhost(), new String[] { "jpy", "btc" }).getData();
            } else if (accountType.equals(LiquiAccountType.SOLR)) {
                accountBalanceResult = hbiTexAccountClient.balance(connid, accountConf.getDawnhost(), new String[] { "btc", "usdt" }).getData();
            }
        } catch (Exception e) {
            LOGGER.error("查询账户余额失败: errorMessage={}", e.getMessage(), e);
        }
        return accountBalanceResult;
    }
    
    /**
     * 读取摆盘账户余额
     * @param symbol
     * @param b 是否强制从接口读取
     * @return
     */
    public DiskAccountBalance readAccountBalance(LiquiAccountType accountType, String symbol, boolean b) {
        String key = null;
        if (accountType.equals(LiquiAccountType.DISK)) {
            key = LiquiPrefixs.ACCOUNT_BALANCE_AND_FROZEN_DISK_PREFIX + symbol.toUpperCase();
        } else if (accountType.equals(LiquiAccountType.SOLR)) {
            key = LiquiPrefixs.ACCOUNT_BALANCE_AND_FROZEN_SOLR_PREFIX + symbol.toUpperCase();
        }
        DiskAccountBalance balance = HttpAssets.parseJson(redis.hasKey(key) ? redis.get(key).toString() : null, new TypeReference<DiskAccountBalance>() {});
        if (balance == null || b) {
            List<AccountBalanceList> accountBalanceResult = this.queryAccountBalance(accountType);
            diskOrderMoneyLimitted.cachedAccountBalance(this.getAccount(accountType), accountType, accountBalanceResult);
            redisProducer.broadcaseAccountBalancess(accountType, accountBalanceResult);
            return HttpAssets.parseJson(redis.get(key).toString(), new TypeReference<DiskAccountBalance>() {});
        }
        return balance;
    }
    
    public HBiTexAccountConf getAccount(LiquiAccountType accountType) {
        String connid = null;
        if (accountType.equals(LiquiAccountType.DISK)) {
            connid = connectService.getConnidDiskConnid();
        } else if (accountType.equals(LiquiAccountType.SOLR)) {
            connid = connectService.getConnidSolrConnid();
        }
        if (connid == null) {
            return null;
        }
        return connectServiceMap.getAccountByConnId(connid, new TypeReference<HBiTexAccountConf>() {});
    }

    // 读取账户余额
    public Map<Object, Object> getBalance(LiquiAccountType accountType) {
        String diskBalanceKey = LiquiPrefixs.ACCOUNT_BALANCE_DISK_PREFIX;
        String solrBalanceKey = LiquiPrefixs.ACCOUNT_BALANCE_SOLR_PREFIX;
        if (accountType.equals(LiquiAccountType.DISK)) {
            return redisHash.hmget(diskBalanceKey);
        }
        if (accountType.equals(LiquiAccountType.SOLR)) {
            return redisHash.hmget(solrBalanceKey);
        }
        return null;
    }

}
