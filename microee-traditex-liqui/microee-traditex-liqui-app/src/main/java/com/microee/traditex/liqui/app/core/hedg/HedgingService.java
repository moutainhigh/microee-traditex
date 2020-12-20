package com.microee.traditex.liqui.app.core.hedg;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;
import com.microee.plugin.commons.LongGenerator;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.jodad.JODAd;
import com.microee.plugin.response.R;
import com.microee.plugin.response.exception.RestException;
import com.microee.plugin.thread.ThreadPoolFactoryLow;
import com.microee.traditex.inbox.oem.constrants.OrderSideEnum;
import com.microee.traditex.inbox.rmi.TradiTexOandaClient;
import com.microee.traditex.liqui.app.components.PricingStreamService;
import com.microee.traditex.liqui.app.core.conn.ConnectBootstrap;
import com.microee.traditex.liqui.app.core.conn.ConnectServiceMap;
import com.microee.traditex.liqui.app.mappers.HedgingOrderTableMapper;
import com.microee.traditex.liqui.app.producer.LiquiKafkaProducer;
import com.microee.traditex.liqui.app.props.LiquisConfProps;
import com.microee.traditex.liqui.app.props.conf.OandaStreamConf;
import com.microee.traditex.liqui.app.service.LiquiRiskService;
import com.microee.traditex.liqui.oem.models.HedgingOrderTable;

// 对冲服务
@Service
public class HedgingService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(HedgingService.class);

    @Autowired
    private PricingStreamService pricingStreamService;
    private static ThreadPoolFactoryLow threadPool =
            ThreadPoolFactoryLow.newInstance("traditex-liqui-多空对冲线程池");

    private Retryer<Boolean> retryer = null;

    @Autowired
    private LongGenerator longGenerator;

    @Autowired
    private ConnectServiceMap connectServiceMap;

    @Autowired
    private ConnectBootstrap setupConnectService;

    @Autowired
    private TradiTexOandaClient oandaClient;

    @Autowired
    private HedgingOrderTableMapper hedgingOrderTableMapper;

    @Autowired
    private LiquiRiskService liquiRiskService;

    @Autowired
    private LiquiKafkaProducer kafkaProducer;

    @Resource
    private LiquisConfProps liquisConfProps;

    @Override
    public void afterPropertiesSet() throws Exception {
        retryer = RetryerBuilder.<Boolean>newBuilder().retryIfException() // 设置异常重试
                .retryIfResult(Predicates.equalTo(true)) // call方法返回true重试
                .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS)) // 设置3秒后重试
                // 设置重试次数超过将出异常, 最多重试 150 次, 超过3此后报警
                .withStopStrategy(StopStrategies.stopAfterAttempt(150)).build();
    }

    public void createOrder(String diskClientOrderId, String matchId, Long tradeTime,
            OrderSideEnum side, BigDecimal filledCashMoney) {
        threadPool.pool().submit(() -> this.createOrderRetry(diskClientOrderId, matchId, tradeTime,
                side, filledCashMoney));
    }

    // 创建对冲订单
    private void createOrderRetry(String diskClientOrderId, String matchId, Long tradeTime,
            OrderSideEnum side, BigDecimal filledCashMoney) {
        try {
            retryer.call(() -> {
                Integer UNIT_RATE = 0;
                BigDecimal units = null;
                BigDecimal price = null;
                // 下单的数量默认除以2 杠杠倍数选2, 杠杆倍数要在子账户设置 之后改不了
                if (side.equals(OrderSideEnum.SELL)) {
                    price = pricingStreamService.readPrice(OrderSideEnum.SELL, "USDJPY");
                } else {
                    price = pricingStreamService.readPrice(OrderSideEnum.BUY, "USDJPY");
                }
                units = filledCashMoney.divide(price, UNIT_RATE, RoundingMode.HALF_DOWN);
                String connid = setupConnectService.getConnidHdegConnid();
                OandaStreamConf oandaStreamConf = connectServiceMap.getAccountByConnId(connid,
                        new TypeReference<OandaStreamConf>() {});
                if (oandaStreamConf == null) {
                    throw new RestException(R.FAILED, "Oanda连接未建立");
                }
                R<Map<String, Object>> createOrderResult = null;
                String jpyBalance = null;
                try {
                    //@formatter:off
                    // {
                    //     "orderRejectTransaction": {
                    //         "id": "724351",
                    //         "accountID": "101-009-16061922-001",
                    //         "userID": 16061922,
                    //         "batchID": "724351",
                    //         "requestID": "78783395853918632",
                    //         "time": "1603556112.269789335",
                    //         "type": "MARKET_ORDER_REJECT",
                    //         "rejectReason": "UNITS_LIMIT_EXCEEDED",
                    //         "instrument": "USD_JPY",
                    //         "units": "119371982",
                    //         "timeInForce": "FOK",
                    //         "positionFill": "DEFAULT",
                    //         "reason": "CLIENT_ORDER"
                    //     },
                    //     "relatedTransactionIDs": [
                    //         "724351"
                    //     ],
                    //     "lastTransactionID": "724351",
                    //     "errorMessage": "The units specified exceeds the maximum number of units allowed",
                    //     "errorCode": "UNITS_LIMIT_EXCEEDED"
                    // }
                    //@formatter:on
                    Map<String, Object> balance = oandaClient
                            .queryAccountBalance(connid, oandaStreamConf.getRestHost(), "JPY")
                            .getData();
                    if (balance.containsKey("JPY")) {
                        jpyBalance = balance.get("JPY").toString();
                        liquiRiskService.alterHedeAccountBalance(new HdegAccountBalance(
                                oandaStreamConf.getAccountId(), "JPY", jpyBalance));
                    } else {
                        LOGGER.error(
                                "多空账户查询余额失败: diskClientOrderId={}, currency={}, filledCashMoney={}, balance={}",
                                diskClientOrderId, "USD", filledCashMoney.toPlainString(),
                                HttpAssets.toJsonString(balance));
                        return true;
                    }
                    createOrderResult = oandaClient.createOrder(connid,
                            oandaStreamConf.getRestHost(), side.name(),
                            oandaStreamConf.getInstrument(), units.setScale(0).toPlainString());
                } catch (Exception e) {
                    LOGGER.error(
                            "创建多空订单异常: diskClientOrderId={}, currency={}, filledCashMoney={}, units={}, errorMessage={}",
                            diskClientOrderId, "USD", filledCashMoney.toPlainString(),
                            units.setScale(0).toPlainString(), e.getMessage(), e);
                    return true;
                }
                if (createOrderResult != null && R.isok(createOrderResult.getCode())) {
                    HedgingOrderTable record = new HedgingOrderTable();
                    record.setOrderId(longGenerator.nextId() + "");
                    record.setDiskClientOrderId(diskClientOrderId);
                    record.setDiskMatchId(matchId);
                    record.setDiskTradeTime(tradeTime);
                    record.setDiskFilledAmount(filledCashMoney.toPlainString());
                    record.setSide(side.name());
                    record.setInstrument(oandaStreamConf.getInstrument());
                    record.setPricing(price);
                    record.setBeforeBalance(new BigDecimal(jpyBalance));
                    record.setUnit(units.setScale(0));
                    record.setCreatedAt(new Date(Instant.now().toEpochMilli()));
                    record.setCreateOrderResult(
                            HttpAssets.toJsonString(createOrderResult.getData()));
                    JSONObject resultJson =
                            new JSONObject(HttpAssets.toJsonString(createOrderResult.getData()));
                    if (resultJson != null && resultJson.has("orderCancelTransaction")) {
                        resultJson = resultJson.getJSONObject("orderCancelTransaction");
                        String hdegResultType =
                                resultJson.has("type") ? resultJson.getString("type") : null;
                        if (hdegResultType != null && hdegResultType.equals("ORDER_CANCEL")) {
                            String _message = String.format(
                                    "报警:创建多空订单被取消: clientOrderId=%s, accountID=%s, OandaOrderId=%s, side=%s, units=%s, hdegResultType=%s, reason=%s --- %s",
                                    diskClientOrderId, resultJson.getString("accountID"),
                                    resultJson.getString("orderID"), side.name(), record.getUnit(),
                                    hdegResultType, resultJson.getString("reason"),
                                    JODAd.format(new Date(Instant.now().toEpochMilli()),
                                            JODAd.STANDARD_FORMAT_MS));
                            LOGGER.error(_message);
                            kafkaProducer.sendMessage(_message);
                            return true;
                        }
                    }
                    try {
                        if (record.getCreateOrderResult().length() > 2048) {
                            record.setCreateOrderResult(
                                    record.getCreateOrderResult().substring(0, 2048));
                        }
                        hedgingOrderTableMapper.insertSelective(record);
                    } catch (Exception e) {
                        LOGGER.error("创建多空订单异常: errorMessage={}", e.getMessage(), e);
                        return true;
                    }
                    LOGGER.info(
                            "创建多空订单成功: diskClientOrderId={}, matchId={}, side={}, amount={}, USDJPY-Price={}, units={}",
                            diskClientOrderId, matchId, side.name(), filledCashMoney, price,
                            units.toPlainString());
                    return false;
                }
                return true;
            });
        } catch (ExecutionException e) {
            LOGGER.error("创建多空订单异常: errorMessage={}", e.getMessage(), e);
        } catch (RetryException e) {
            LOGGER.error("创建多空订单异常: errorMessage={}", e.getMessage(), e);
        }
    }

}
