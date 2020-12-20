package com.microee.traditex.liqui.app.components;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.stacks.redis.support.Redis;
import com.microee.traditex.inbox.oem.hbitex.vo.AccountBalanceList;
import com.microee.traditex.liqui.app.constrants.LiquiPrefixs;
import com.microee.traditex.liqui.app.core.disk.DiskOrderParam;
import com.microee.traditex.liqui.app.producer.LiquiKafkaProducer;
import com.microee.traditex.liqui.app.props.conf.HBiTexAccountConf;
import com.microee.traditex.liqui.app.service.LiquiRiskService;
import com.microee.traditex.liqui.oem.enums.LiquiAccountType;
import com.microee.traditex.liqui.oem.models.LiquiRiskStrategySettings;

@Component
public class DiskOrderMoneyLimitted {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskOrderMoneyLimitted.class);

    @Autowired
    private Redis redis;

    @Autowired
    private LiquiRiskSettings liquiConf;

    @Autowired
    private LiquiRiskService liquiRiskService;

    @Autowired
    private LiquiKafkaProducer kafkaProducer;
    
    public void cachedAccountBalance(HBiTexAccountConf accountConf, LiquiAccountType accountType, List<AccountBalanceList> accountBalanceList) {
        if (accountBalanceList == null) {
            return;
        }
        if (accountType.equals(LiquiAccountType.DISK)) {
            //@formatter:off
            List<AccountBalanceList> _btcBalance = accountBalanceList.stream().filter(f -> f.getCurrency().equals("btc")).collect(Collectors.toList());
            AccountBalanceList _btcBalanceTrad = _btcBalance.stream().filter(m -> m.getType().equals("trade")).findFirst().orElse(null);
            AccountBalanceList _btcBalanceFrozen = _btcBalance.stream().filter(m -> m.getType().equals("frozen")).findFirst().orElse(null);
            List<AccountBalanceList> _jpyBalance = accountBalanceList.stream().filter(f -> f.getCurrency().equals("jpy")).collect(Collectors.toList());
            AccountBalanceList _jpyBalanceTrad = _jpyBalance.stream().filter(m -> m.getType().equals("trade")).findFirst().orElse(null);
            AccountBalanceList _jpyBalanceFrozen = _jpyBalance.stream().filter(m -> m.getType().equals("frozen")).findFirst().orElse(null);
            //@formatter:on
            DiskAccountBalance btcBalance = new DiskAccountBalance(accountConf.getUid(), "BTC", _btcBalanceTrad, _btcBalanceFrozen);
            DiskAccountBalance jpyBalance = new DiskAccountBalance(accountConf.getUid(), "JPY", _jpyBalanceTrad, _jpyBalanceFrozen);
            redis.set(LiquiPrefixs.ACCOUNT_BALANCE_AND_FROZEN_DISK_PREFIX + "BTC", HttpAssets.toJsonString(btcBalance));
            redis.set(LiquiPrefixs.ACCOUNT_BALANCE_AND_FROZEN_DISK_PREFIX + "JPY", HttpAssets.toJsonString(jpyBalance));
            liquiRiskService.alertDiskAccountBalance(accountConf, btcBalance, jpyBalance);
        }
        if (accountType.equals(LiquiAccountType.SOLR)) {
            //@formatter:off
            List<AccountBalanceList> _btcBalance = accountBalanceList.stream().filter(f -> f.getCurrency().equals("btc")).collect(Collectors.toList());
            AccountBalanceList _btcBalanceTrad = _btcBalance.stream().filter(m -> m.getType().equals("trade")).findFirst().orElse(null);
            AccountBalanceList _btcBalanceFrozen = _btcBalance.stream().filter(m -> m.getType().equals("frozen")).findFirst().orElse(null);
            List<AccountBalanceList> _usdtBalance = accountBalanceList.stream().filter(f -> f.getCurrency().equals("usdt")).collect(Collectors.toList());
            AccountBalanceList _usdtBalanceTrad = _usdtBalance.stream().filter(m -> m.getType().equals("trade")).findFirst().orElse(null);
            AccountBalanceList _usdtBalanceFrozen = _usdtBalance.stream().filter(m -> m.getType().equals("frozen")).findFirst().orElse(null);
            //@formatter:on
            DiskAccountBalance btcBalance = new DiskAccountBalance(accountConf.getUid(), "BTC", _btcBalanceTrad, _btcBalanceFrozen);
            DiskAccountBalance usdtBalance = new DiskAccountBalance(accountConf.getUid(), "JPY", _usdtBalanceTrad, _usdtBalanceFrozen);
            redis.set(LiquiPrefixs.ACCOUNT_BALANCE_AND_FROZEN_SOLR_PREFIX + "BTC", HttpAssets.toJsonString(btcBalance));
            redis.set(LiquiPrefixs.ACCOUNT_BALANCE_AND_FROZEN_SOLR_PREFIX + "USDT", HttpAssets.toJsonString(usdtBalance));
            liquiRiskService.alertSolrAccountBalance(accountConf, btcBalance, usdtBalance);  
        }
    }
    
    // 金额是否超过上限
    public Boolean isMoneyLimitted(String symbol, BigDecimal orderMoney, DiskAccountBalance balance, DiskOrderParam diskOrderParam) {
        if (balance == null) {
            LOGGER.warn("摆盘资金数据没有读取到: symbol={}, orderMoney={}", symbol, orderMoney);
            return true;
        }
        LiquiRiskStrategySettings strategySettings = liquiConf.getCachedStrategySettings("btcusdt");
        if (strategySettings == null || strategySettings.getBuyMoneyLimit() == null) {
            LOGGER.info("没有配置摆盘资金上限: symbol={}, currentBalanceRate={}", symbol, balance.getBalanceRate());
            return true;
        }
        BigDecimal tradBalance = balance.getTradeBalance();
        BigDecimal frozenBalance = balance.getFrozenBalance();
        DiskAccountBalance newBalance = new DiskAccountBalance(symbol, tradBalance.subtract(orderMoney), frozenBalance.add(orderMoney));
        BigDecimal limited = new BigDecimal(strategySettings.getBuyMoneyLimit());
        String newBalanceRateString = newBalance.getBalanceRate().multiply(new BigDecimal("100")).setScale(6, BigDecimal.ROUND_DOWN).toPlainString() + "%";
        String oldBalanceRateString = balance.getBalanceRate().multiply(new BigDecimal("100")).setScale(6, BigDecimal.ROUND_DOWN).toPlainString() + "%";
        String limittedString = limited.multiply(new BigDecimal("100")).setScale(6, BigDecimal.ROUND_DOWN).toPlainString() + "%";
        if (tradBalance.compareTo(orderMoney) == -1) {
            String _message = String.format("摆盘账户余额不足报警: symbol=%s, tradBalance=%s, orderMoney=%s, limittedRate=%s, diskOrderParam=%s", 
                    symbol, tradBalance, orderMoney, limittedString, HttpAssets.toJsonString(diskOrderParam));
            kafkaProducer.sendMessage(_message);
            LOGGER.info(_message);
            return true;
        } else if (newBalance.getBalanceRate().compareTo(limited) == 1) {
            LOGGER.info("摆盘资金超过上限: symbol={}, newRate={}, oldRate={}, limittedRate={}, diskOrderParam={}", 
                    symbol, newBalanceRateString, oldBalanceRateString, limittedString, HttpAssets.toJsonString(diskOrderParam));
            return true;
        }
        LOGGER.info("摆盘资金上限验证通过: symbol={}, newRate={}, oldRate={}, limittedRate={}", symbol, newBalanceRateString, oldBalanceRateString, limittedString);
        return false;
    }
    
    // 金额是否超过上限
    public Boolean isAmountLimitted(String symbol, BigDecimal orderAmount, DiskAccountBalance balance, DiskOrderParam diskOrderParam) {
        if (balance == null) {
            LOGGER.warn("摆盘资金数据没有读取到: symbol={}, orderAmount={}", symbol, orderAmount);
            return true;
        }
        LiquiRiskStrategySettings strategySettings = liquiConf.getCachedStrategySettings("btcusdt");
        if (strategySettings == null || strategySettings.getBuyMoneyLimit() == null) {
            LOGGER.info("没有配置摆盘数量上限: symbol={}, currentBalanceRate={}", symbol, balance.getBalanceRate());
            return true;
        }
        BigDecimal tradBalance = balance.getTradeBalance();
        BigDecimal frozenBalance = balance.getFrozenBalance();
        DiskAccountBalance newBalance = new DiskAccountBalance(symbol, tradBalance.subtract(orderAmount), frozenBalance.add(orderAmount));
        BigDecimal limited = new BigDecimal(strategySettings.getSellMoneyLimit());
        String newBalanceRateString = newBalance.getBalanceRate().multiply(new BigDecimal("100")).setScale(6, BigDecimal.ROUND_DOWN).toPlainString() + "%";
        String oldBalanceRateString = balance.getBalanceRate().multiply(new BigDecimal("100")).setScale(6, BigDecimal.ROUND_DOWN).toPlainString() + "%";
        String limittedString = limited.multiply(new BigDecimal("100")).setScale(6, BigDecimal.ROUND_DOWN).toPlainString() + "%";
        if (tradBalance.compareTo(orderAmount) == -1) {
            String _message = String.format("摆盘账户余额不足报警: symbol=%s, tradBalance=%s, orderAmount=%s, limittedRate=%s, diskOrderParam=%s", 
                    symbol, tradBalance, orderAmount, limittedString, HttpAssets.toJsonString(diskOrderParam));
            kafkaProducer.sendMessage(_message);
            LOGGER.info(_message);
            return true;
        } else if (newBalance.getBalanceRate().compareTo(limited) == 1) {
            LOGGER.info("摆盘数量超过上限: symbol={}, newRate={}, oldRate={}, limittedRate={}, diskOrderParam={}", 
                    symbol, newBalanceRateString, oldBalanceRateString, limittedString, HttpAssets.toJsonString(diskOrderParam));
            return true;
        }
        return false;
    }

    public static class DiskAccountBalance implements Serializable {
        
        private static final long serialVersionUID = 1L;

        private String uid;
        private String symbol;
        private BigDecimal tradeBalance;
        private BigDecimal frozenBalance;
        private BigDecimal balanceRate;

        public DiskAccountBalance() {
            
        }
        
        public DiskAccountBalance(String symbol, BigDecimal tradBalance, BigDecimal frozenBalance) {
            this.symbol = symbol;
            this.tradeBalance = tradBalance == null ? BigDecimal.ZERO : tradBalance;
            this.frozenBalance = frozenBalance == null ? BigDecimal.ZERO : frozenBalance;
            this.balanceRate = this.tradeBalance.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal(0.0d) : this.frozenBalance.divide(this.tradeBalance, 18, RoundingMode.HALF_DOWN);
        }
        
        public DiskAccountBalance(String uid, String symbol, AccountBalanceList tradBalance, AccountBalanceList frozenBalance) {
            this.uid = uid;
            this.symbol = symbol;
            this.tradeBalance = tradBalance == null || tradBalance.getBalance() == null || tradBalance.getBalance().trim().isEmpty() ? BigDecimal.ZERO : new BigDecimal(tradBalance.getBalance());
            this.frozenBalance = frozenBalance == null || frozenBalance.getBalance() == null || frozenBalance.getBalance().trim().isEmpty() ? BigDecimal.ZERO : new BigDecimal(frozenBalance.getBalance());
            this.balanceRate = this.tradeBalance.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal(0.0d) : this.frozenBalance.divide(this.tradeBalance, 18, RoundingMode.HALF_DOWN);
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public BigDecimal getFrozenBalance() {
            return frozenBalance;
        }

        public void setFrozenBalance(BigDecimal frozenBalance) {
            this.frozenBalance = frozenBalance;
        }

        public BigDecimal getTradeBalance() {
            return tradeBalance;
        }

        public void setTradeBalance(BigDecimal tradeBalance) {
            this.tradeBalance = tradeBalance;
        }

        public BigDecimal getBalanceRate() {
            return balanceRate;
        }

        public void setBalanceRate(BigDecimal balanceRate) {
            this.balanceRate = balanceRate;
        }

    }
}
