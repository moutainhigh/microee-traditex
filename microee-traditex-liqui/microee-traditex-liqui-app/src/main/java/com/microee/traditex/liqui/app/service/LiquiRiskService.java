package com.microee.traditex.liqui.app.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.jodad.JODAd;
import com.microee.traditex.liqui.app.components.DiskOrderMoneyLimitted.DiskAccountBalance;
import com.microee.traditex.liqui.app.components.LiquiRiskSettings;
import com.microee.traditex.liqui.app.core.conn.ConnectBootstrap;
import com.microee.traditex.liqui.app.core.hedg.HdegAccountBalance;
import com.microee.traditex.liqui.app.mappers.LiquiRiskStrategySettingsMapper;
import com.microee.traditex.liqui.app.producer.LiquiKafkaProducer;
import com.microee.traditex.liqui.app.props.conf.HBiTexAccountConf;
import com.microee.traditex.liqui.oem.enums.LiquiAccountType;
import com.microee.traditex.liqui.oem.models.LiquiRiskAlarmSettings;
import com.microee.traditex.liqui.oem.models.LiquiRiskStrategySettings;

@Service
public class LiquiRiskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiquiRiskService.class);
    
    @Autowired
    private LiquiRiskSettings liquiRiskSettings;

    @Autowired
    private ConnectBootstrap setupConnectService;

    @Autowired
    private LiquiRiskStrategySettingsMapper riskStrategySettingsMapper;
    
    @Autowired
    private LiquiKafkaProducer kafkaProducer;

    @Autowired
    private LiquiRiskSettings liquiConf;
    
    public void alertNetwork(String connid, String schema, String host, String path, Long start, Long speed) {
        String solrConnid = setupConnectService.getConnidSolrConnid();
        String diskConnid = setupConnectService.getConnidDiskConnid();
        String hdegConnid = setupConnectService.getConnidHdegConnid();
        LiquiAccountType accountType = null;
        LiquiRiskAlarmSettings liquiRiskAlarmSettings = liquiRiskSettings.getCachedAlarmSettings("btcusdt");
        Long warnNetworkDelay = -1l;
        String warnFieldName = null;
        String fuseFieldName = null;
        Long fuseNetworkDelay = -1l;
        if (liquiRiskAlarmSettings == null) {
            String _message = String.format("没有配置流动性网络延迟报警: alertNetwork#LiquiRiskAlarmSettings");
            kafkaProducer.sendMessage(_message);
            return;
        }
        if (connid.equals(solrConnid)) {
            accountType = LiquiAccountType.SOLR;
            if (liquiRiskAlarmSettings.getDiskWarnNetworkDelay() != null) {
                warnNetworkDelay = Long.parseLong(liquiRiskAlarmSettings.getDiskWarnNetworkDelay());
                warnFieldName = "diskWarnNetworkDelay";
            }
            if (liquiRiskAlarmSettings.getDiskFuseNetworkDelay() != null) {
                fuseNetworkDelay = Long.parseLong(liquiRiskAlarmSettings.getDiskFuseNetworkDelay());
                fuseFieldName = "diskFuseNetworkDelay";
            }
        }
        if (connid.equals(diskConnid)) {
            accountType = LiquiAccountType.DISK;
            if (liquiRiskAlarmSettings.getLiquidityWarnNetworkDelay() != null) {
                warnNetworkDelay = Long.parseLong(liquiRiskAlarmSettings.getLiquidityWarnNetworkDelay());
                warnFieldName = "liquidityWarnNetworkDelay";
            }
            if (liquiRiskAlarmSettings.getLiquidityFuseNetworkDelay() != null) {
                fuseNetworkDelay = Long.parseLong(liquiRiskAlarmSettings.getLiquidityFuseNetworkDelay());
                fuseFieldName = "liquidityFuseNetworkDelay";
            }
        }
        if (connid.equals(hdegConnid)) {
            accountType = LiquiAccountType.HDEG;
            if (liquiRiskAlarmSettings.getExchangeWarnNetworkDelay() != null) {
                warnNetworkDelay = Long.parseLong(liquiRiskAlarmSettings.getExchangeWarnNetworkDelay());
                warnFieldName = "exchangeWarnNetworkDelay";
            }
            if (liquiRiskAlarmSettings.getExchangeFuseNetworkDelay() != null) {
                fuseNetworkDelay = Long.parseLong(liquiRiskAlarmSettings.getExchangeFuseNetworkDelay());
                fuseFieldName = "exchangeFuseNetworkDelay";
            }
        }
        if (accountType == null) {
            return;
        }
        if (warnNetworkDelay != -1 && speed > warnNetworkDelay) {
            String _message = String.format("[%s]网路延迟达到设定的阈值报警: conditon=%s%s, 起始时间=%s, 耗时=%s毫秒, 阈值=%s毫秒, url=%s://%s%s --- %s",
                    accountType.desc, "speed>", warnFieldName, JODAd.format(new Date(start), JODAd.STANDARD_FORMAT_MS), speed, warnNetworkDelay, schema, host, path, JODAd.format(new Date(Instant.now().toEpochMilli()), JODAd.STANDARD_FORMAT_MS));
            kafkaProducer.sendMessage(_message); 
            LOGGER.info(_message);
        } if (fuseNetworkDelay != -1 && speed > fuseNetworkDelay) {
            triggerHistry();
            String _message = String.format("[%s]网路延迟达到设定的阈值熔断: conditon=%s%s, 起始时间=%s, 耗时=%s毫秒, 阈值=%s毫秒, url=%s://%s%s --- %s", 
                    accountType.desc, "speed>", fuseFieldName, JODAd.format(new Date(start), JODAd.STANDARD_FORMAT_MS), speed, fuseNetworkDelay, schema, host, path, JODAd.format(new Date(Instant.now().toEpochMilli()), JODAd.STANDARD_FORMAT_MS));
            kafkaProducer.sendMessage(_message);
            LOGGER.info(_message);
        }
    }

    // 摆盘账户余额监控
    public void alertDiskAccountBalance(HBiTexAccountConf accountConf, DiskAccountBalance btcBalance, DiskAccountBalance jpyBalance) {
        LiquiRiskAlarmSettings settings = liquiRiskSettings.getCachedAlarmSettings("btcusdt");
        if (settings == null) {
            String _message = String.format("报警:未配置风控报警策略参数: alertDiskAccountBalance#LiquiRiskAlarmSettings");
            kafkaProducer.sendMessage(_message);
            return;
        }
        String s1 = settings.getLiquidityWarnDiskBasePos(); // 流动性账户对冲交易对base仓位 例如btc
        String s2 = settings.getLiquidityWarnExchangeQuotePos(); // 流动性账户汇率对冲交易对quote 例如jpy
        String s3 = settings.getLiquidityFuseDiskBasePos();
        String s4 = settings.getLiquidityFuseExchangeQuotePos();
        if (btcBalance.getTradeBalance().compareTo(new BigDecimal(s1)) == -1) {
            String _message = String.format("[流动性账户]账户余额报警: uid=%s, currency=%s, balance=%s, 阈值=%s --- %s", accountConf.getUid(), "btc", btcBalance.getTradeBalance(), s1, JODAd.format(new Date(Instant.now().toEpochMilli()), JODAd.STANDARD_FORMAT_MS));
            kafkaProducer.sendMessage(_message);
            LOGGER.info(_message);
        }
        if (jpyBalance.getTradeBalance().compareTo(new BigDecimal(s2)) == -1) {
            String _message = String.format("[流动性账户]账户余额报警: uid=%s, currency=%s, balance=%s, 阈值=%s --- %s", accountConf.getUid(), "jpy", jpyBalance.getTradeBalance(), s2, JODAd.format(new Date(Instant.now().toEpochMilli()), JODAd.STANDARD_FORMAT_MS));
            kafkaProducer.sendMessage(_message);
            LOGGER.info(_message);
        }
        if (btcBalance.getTradeBalance().compareTo(new BigDecimal(s3)) == -1) {
            triggerHistry();
            String _message = String.format("[流动性账户]账户余额熔断报警: uid=%s, currency=%s, balance=%s, 阈值=%s --- %s", accountConf.getUid(), "btc", btcBalance.getTradeBalance(), s3, JODAd.format(new Date(Instant.now().toEpochMilli()), JODAd.STANDARD_FORMAT_MS));
            kafkaProducer.sendMessage(_message);
            LOGGER.info(_message);
        }
        if (jpyBalance.getTradeBalance().compareTo(new BigDecimal(s4)) == -1) {
            triggerHistry();
            String _message = String.format("[流动性账户]账户余额熔断报警: uid=%s, currency=%s, balance=%s, 阈值=%s --- %s", accountConf.getUid(), "jpy", jpyBalance.getTradeBalance(), s4, JODAd.format(new Date(Instant.now().toEpochMilli()), JODAd.STANDARD_FORMAT_MS));
            kafkaProducer.sendMessage(_message);
            LOGGER.info(_message);
        }
    }

    // 甩单账户余额监控
    public void alertSolrAccountBalance(HBiTexAccountConf accountConf, DiskAccountBalance btcBalance, DiskAccountBalance usdtBalance) {
        LiquiRiskAlarmSettings settings = liquiRiskSettings.getCachedAlarmSettings("btcusdt");
        if (settings == null) {
            LOGGER.error("未配置风控报警策略参数: btcBalance={}, usdtBalance={}", HttpAssets.toJsonString(btcBalance), HttpAssets.toJsonString(usdtBalance));
            return;
        }
        String s1 = settings.getDiskWarnDiskBasePos(); // 盘面账户对冲交易对base仓位 例如btc
        String s2 = settings.getDiskWarnDiskQuotePos(); // 盘面账户汇率对冲交易对quote 例如usdt
        String s3 = settings.getDiskFuseDiskBasePos();
        String s4 = settings.getDiskFuseDiskQuotePos();
        if (btcBalance.getTradeBalance().compareTo(new BigDecimal(s1)) == -1) {
            String _message = String.format("[盘面对冲账户]账户余额报警: uid=%s, currency=%s, balance=%s, 阈值=%s --- %s", accountConf.getUid(), "btc", btcBalance.getTradeBalance(), s1, JODAd.format(new Date(Instant.now().toEpochMilli()), JODAd.STANDARD_FORMAT_MS));
            kafkaProducer.sendMessage(_message);
            LOGGER.info(_message);
        }
        if (usdtBalance.getTradeBalance().compareTo(new BigDecimal(s2)) == -1) {
            String _message = String.format("[盘面对冲账户]账户余额报警: uid=%s, currency=%s, balance=%s, 阈值=%s --- %s", accountConf.getUid(), "usdt", usdtBalance.getTradeBalance(), s2, JODAd.format(new Date(Instant.now().toEpochMilli()), JODAd.STANDARD_FORMAT_MS));
            kafkaProducer.sendMessage(_message);
            LOGGER.info(_message);
        }
        if (btcBalance.getTradeBalance().compareTo(new BigDecimal(s3)) == -1) {
            triggerHistry();
            String _message = String.format("[盘面对冲账户]账户余额熔断报警: uid=%s, currency=%s, balance=%s, 阈值=%s --- %s", accountConf.getUid(), "btc", btcBalance.getTradeBalance(), s3, JODAd.format(new Date(Instant.now().toEpochMilli()), JODAd.STANDARD_FORMAT_MS));
            kafkaProducer.sendMessage(_message);
            LOGGER.info(_message);
        }
        if (usdtBalance.getTradeBalance().compareTo(new BigDecimal(s4)) == -1) {
            triggerHistry();
            String _message = String.format("[盘面对冲账户]账户余额熔断报警: uid=%s, currency=%s, balance=%s, 阈值=%s --- %s", accountConf.getUid(), "usdt", usdtBalance.getTradeBalance(), s4, JODAd.format(new Date(Instant.now().toEpochMilli()), JODAd.STANDARD_FORMAT_MS));
            kafkaProducer.sendMessage(_message);
            LOGGER.info(_message);
        }
    }
    
    public void alterHedeAccountBalance(HdegAccountBalance account) {
        LiquiRiskAlarmSettings settings = liquiRiskSettings.getCachedAlarmSettings("btcusdt");
        if (settings == null) {
            LOGGER.error("未配置风控报警策略参数: hdegAccount={}", HttpAssets.toJsonString(account));
            return;
        }
        String s1 = settings.getExchangeWarnExchangeQuotePos();
        String s2 = settings.getExchangeFuseExchangeQuotePos();
        if (new BigDecimal(account.getBalance()).compareTo(new BigDecimal(s1)) == -1) {
            String _message = String.format("[汇率对冲账户]账户余额报警: uid=%s, currency=%s, balance=%s, 阈值=%s --- %s", account.getAccount(), account.getInstrument(), account.getBalance(), s1, JODAd.format(new Date(Instant.now().toEpochMilli()), JODAd.STANDARD_FORMAT_MS));
            kafkaProducer.sendMessage(_message);
            LOGGER.info(_message);
        }
        if (new BigDecimal(account.getBalance()).compareTo(new BigDecimal(s2)) == -1) {
            triggerHistry();
            String _message = String.format("[汇率对冲账户]账户余额熔断报警: uid=%s, currency=%s, balance=%s, 阈值=%s --- %s", account.getAccount(), account.getInstrument(), account.getBalance(), s2, JODAd.format(new Date(Instant.now().toEpochMilli()), JODAd.STANDARD_FORMAT_MS));
            kafkaProducer.sendMessage(_message);
            LOGGER.info(_message);
        }
    }
    
    // 触发熔断
    public void triggerHistry() {
        List<LiquiRiskStrategySettings> list = riskStrategySettingsMapper.selectByIdAndSymbol(null, null);
        for (int i=0; i<list.size(); i++) {
            list.get(i).setBuyState(2);
            list.get(i).setSellState(2);;
            riskStrategySettingsMapper.updateByPrimaryKeySelective(list.get(i));
            liquiConf.cachedStrategySettings(list.get(i));
        }
    }

}
