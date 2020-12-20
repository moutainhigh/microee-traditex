package com.microee.traditex.liqui.app.components;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.stacks.redis.support.Redis;
import com.microee.traditex.inbox.oem.constrants.OrderSideEnum;
import com.microee.traditex.liqui.app.constrants.LiquiPrefixs;
import com.microee.traditex.liqui.oem.models.LiquiRiskAlarmSettings;
import com.microee.traditex.liqui.oem.models.LiquiRiskStrategySettings;

@Component
public class LiquiRiskSettings {

    public static final BigDecimal USDT_USD_RATE = new BigDecimal("1.0007");

    @Autowired
    private Redis redis;
    
    public BigDecimal getUSDTUSDRate() {
        LiquiRiskStrategySettings settings = this.getCachedStrategySettings("btcusdt");
        if (settings == null || settings.getExchangeConfig() == null || settings.getExchangeConfig().trim().isEmpty()) {
            return USDT_USD_RATE;
        }
        return new BigDecimal(settings.getExchangeConfig());
    }
    
    public int getSolrBuyPrecson() {
        LiquiRiskStrategySettings settings = this.getCachedStrategySettings("btcusdt");
        if (settings != null) {
            if (settings.getDiskBuyPrecson() != null) {
                return settings.getDiskBuyPrecson();
            }
        }
        return 4;
    }
    
    public int getSolrSellPrecson() {
        LiquiRiskStrategySettings settings = this.getCachedStrategySettings("btcusdt");
        if (settings != null) {
            if (settings.getDiskSellPrecson() != null) {
                return settings.getDiskSellPrecson();
            }
        }
        return 4;
    }
    
    public BigDecimal getSolrPriceScaleMax() {
        LiquiRiskStrategySettings settings = this.getCachedStrategySettings("btcusdt");
        if (settings != null) {
            if (settings.getDiskPriceScaleMax() != null) {
                return new BigDecimal(settings.getDiskPriceScaleMax());
            }
        }
        return new BigDecimal("0.015");
    }
    
    public BigDecimal getSolrPriceScaleMin() {
        LiquiRiskStrategySettings settings = this.getCachedStrategySettings("btcusdt");
        if (settings != null) {
            if (settings.getDiskPriceScaleMin() != null) {
                return new BigDecimal(settings.getDiskPriceScaleMin());
            }
        }
        return new BigDecimal("0.015");
    }
    
    public boolean getDiskLiquisEnableState(String symbol, OrderSideEnum side) {
        LiquiRiskStrategySettings setting = this.getCachedStrategySettings(symbol);
        if (setting == null) {
            return false;
        }
        if (side.equals(OrderSideEnum.BUY)) {
            return setting.getBuyState().equals(1);
        }
        return setting.getSellState().equals(1);
    }

    // 取得流动性摆盘配置
    public LiquiRiskStrategySettings getCachedStrategySettings(String symbol) {
        String key = LiquiPrefixs.LIQUI_STRATEGY_SETTINGS + symbol.toUpperCase();
        if (!redis.hasKey(key)) {
            return null;
        }
        return HttpAssets.parseJson(redis.get(key).toString(), new TypeReference<LiquiRiskStrategySettings>() {});
    }
    
    // 缓存流动性摆盘配置
    public void cachedStrategySettings(LiquiRiskStrategySettings settings) {
        String key = LiquiPrefixs.LIQUI_STRATEGY_SETTINGS + (settings.getDiskBase() + settings.getDiskQuote()).toUpperCase();
        redis.set(key, HttpAssets.toJsonString(settings));
    }

    // 取得流动性摆盘风控报警配置
    public LiquiRiskAlarmSettings getCachedAlarmSettings(String symbol) {
        String key = LiquiPrefixs.LIQUI_ALARM_SETTINGS + symbol.toUpperCase();
        if (!redis.hasKey(key)) {
            return null;
        }
        return HttpAssets.parseJson(redis.get(key).toString(), new TypeReference<LiquiRiskAlarmSettings>() {});
    }
    
    // 缓存流动性摆盘风控报警配置
    public void cachedAlarmSettings(LiquiRiskAlarmSettings settings) {
        String key = LiquiPrefixs.LIQUI_ALARM_SETTINGS + (settings.getDiskBase() + settings.getDiskQuote()).toUpperCase();
        redis.set(key, HttpAssets.toJsonString(settings));
    }
    
}
