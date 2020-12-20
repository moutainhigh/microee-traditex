package com.microee.traditex.liqui.app.components;

import java.math.BigDecimal;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.microee.traditex.inbox.oem.constrants.OrderSideEnum;
import com.microee.traditex.inbox.oem.constrants.SymbolEnums;
import com.microee.traditex.inbox.oem.constrants.VENDER;

@Component
public class TranferPriceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranferPriceService.class);

    @Autowired
    private PricingStreamService pricingStreamService;

    @Autowired
    private LiquiRiskSettings liquiConf;
    
    // 从外汇市场获取汇率
    // 根据配置获取 usdt/usd 价格比率
    // 价格转换, e.g: btc/usdt to btc/jpy
    public JSONObject tranferPrice(String theOrderBookId, String connid, VENDER vender, String symbol, OrderSideEnum side,
            JSONArray sourceOrder) {
        BigDecimal usdtPrice = BigDecimal.valueOf(sourceOrder.getDouble(0));
        BigDecimal amount = BigDecimal.valueOf(sourceOrder.getDouble(1));
        JSONObject linkJSONObject = new JSONObject();
        JSONObject source = new JSONObject();
        source.put("theOrderBookId", theOrderBookId);
        source.put("vender", vender);
        source.put("symbol", symbol);
        source.put("side", side.name());
        source.put("price", usdtPrice);
        source.put("amount", amount);
        linkJSONObject.put("source", source);
        if (symbol.equalsIgnoreCase(SymbolEnums.BTCUSDT.code)) {
            BigDecimal USDT_USD_RATE = liquiConf.getUSDTUSDRate();
            BigDecimal USD_JPY_RATE = pricingStreamService.readPrice(side, SymbolEnums.USDJPY.name());
            // BTC/USDT to BTC/JPY
            JSONObject target = new JSONObject();
            target.put("USDT_USD_RATE", USDT_USD_RATE.toPlainString());
            target.put("USD_JPY_RATE", USD_JPY_RATE.toPlainString());
            target.put("symbol", SymbolEnums.BTCJPY.name());
            target.put("side", side.name());
            target.put("USDT_PRICE", usdtPrice.toPlainString());
            target.put("price", convertToBTCJPY(USDT_USD_RATE, USD_JPY_RATE, side, usdtPrice));
            target.put("amount", amount);
            linkJSONObject.put("target", target);
            return linkJSONObject;
        }
        LOGGER.warn("不支持的交易对, 已忽略={}", source.toString());
        return null;
    }


    // 量价处理转换: 流动性交易对是 btcjpy, 盘面搬运交易对是 btcusdt, 汇率对冲交易对是 usdjpy
    public BigDecimal convertToBTCJPY(BigDecimal USDT_USD_RATE, BigDecimal USD_JPY_RATE, OrderSideEnum side, BigDecimal usdtPrice) {
        // USD 是美元，是 United States dollar 的缩写, 目前1美元(即1USD)可以兑换6.5128人民币
        // 泰达币（USDT）是Tether公司推出的基于稳定价值货币美元（USD）的代币, 是一种将加密货币与法定货币美元挂钩的虚拟货币, 基本上一个泰达币价值就等1美元。
        BigDecimal usdPrice = usdtPrice.multiply(USDT_USD_RATE);
        return usdPrice.multiply(USD_JPY_RATE); // BTC/JPY
    }

}
