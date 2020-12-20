package com.microee.traditex.liqui.app.core.solr;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.thread.ThreadPoolFactoryScheduled;
import com.microee.traditex.inbox.oem.constrants.OrderSideEnum;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.liqui.app.components.LiquiRiskSettings;
import com.microee.traditex.liqui.app.mappers.SolrNextOrderTableMapper;
import com.microee.traditex.liqui.oem.models.SolrNextOrderTable;
import com.microee.traditex.liqui.oem.models.SolrOrderTable;

@Service
public class SolrNextOrderService implements InitializingBean  {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrNextOrderService.class);
    private static ThreadPoolFactoryScheduled threadPool = ThreadPoolFactoryScheduled.newInstance();
    private static final int SOLR_NEXT_ORDER_DURATION = 5;

    @Autowired
    private SolrNextOrderTableMapper solrNextOrderTableMapper;

    @Autowired
    private SolrOrderService solrOrderService;

    @Autowired
    private LiquiRiskSettings liquiConf;

    public void takeANextSolrOrder() {
        SolrNextOrderTable nextSolrOrder = solrNextOrderTableMapper.selectNextSolrOrder();
        if (nextSolrOrder != null) {
            LOGGER.info("甩下一个订单: nextSolrOrder={}", HttpAssets.toJsonString(nextSolrOrder));
            Integer solrBuyPrec = liquiConf.getSolrBuyPrecson(); // 读取配置
            Integer solrSellPrec = liquiConf.getSolrSellPrecson();
            OrderSideEnum solrSide = OrderSideEnum.valueOf(nextSolrOrder.getSolrSide());
            BigDecimal solrQuantity = nextSolrOrder.getNextSolrQuantity();
            String solrType = solrSide.name().equals("BUY") ? "buy-market" : "sell-market";
            BigDecimal solrOrderAmount = null;
            Integer solrAmountPrec = null;
            if (solrType.equals("buy-market")) {
                solrOrderAmount = solrQuantity.setScale(solrBuyPrec, BigDecimal.ROUND_DOWN);
                solrAmountPrec = solrBuyPrec;
            } else {
                solrOrderAmount = solrQuantity.setScale(solrSellPrec, BigDecimal.ROUND_DOWN);
                solrAmountPrec = solrSellPrec;
            }
            SolrOrderTable theOrder = new SolrOrderTable();
            theOrder.setSolrClientOrderId(nextSolrOrder.getNextSolrId().toString());
            theOrder.setDiskResultOrderId(nextSolrOrder.getDiskResultOrderId());
            theOrder.setDiskSymbol(nextSolrOrder.getDiskSymbol());
            theOrder.setVender(VENDER.HBiTex.code);
            theOrder.setSolrSymbol(nextSolrOrder.getSolrSymbol());
            theOrder.setSolrSide(nextSolrOrder.getSolrSide());
            theOrder.setSolrAmount(nextSolrOrder.getNextSolrQuantity());
            theOrder.setSolrPrice(null);
            theOrder.setSolrOrderAmount(solrOrderAmount);
            theOrder.setSolrOrderPrice(null);
            theOrder.setSolrOrderType(nextSolrOrder.getSolrOrderType());
            theOrder.setSolrPricePrec(null);
            theOrder.setSolrAmountPrec(solrAmountPrec);
            theOrder.setUsdCurrencyPricing(BigDecimal.ZERO);
            theOrder.setUsdtUsdRate(BigDecimal.ZERO);
            solrOrderService.calculateSolrOrderByOrderbook(OrderSideEnum.valueOf(nextSolrOrder.getSolrSide()), theOrder); 
            solrOrderService.solrOrderRetryer(theOrder);
        }
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        threadPool.pool().scheduleAtFixedRate(() -> this.takeANextSolrOrder(), 35, SOLR_NEXT_ORDER_DURATION, TimeUnit.SECONDS);
    }
    
}
