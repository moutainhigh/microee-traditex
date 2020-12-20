package com.microee.traditex.liqui.app.core.solr;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONArray;
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
import com.microee.plugin.commons.UUIDObject;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.thread.ThreadPoolFactoryLow;
import com.microee.stacks.redis.support.RedisHash;
import com.microee.traditex.inbox.oem.constrants.OrderSideEnum;
import com.microee.traditex.inbox.oem.constrants.SymbolEnums;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.inbox.oem.hbitex.HBiTexHttpResult;
import com.microee.traditex.inbox.oem.hbitex.vo.HBiTexOrderDetails;
import com.microee.traditex.inbox.rmi.TradiTexHBiTexOrderClient;
import com.microee.traditex.liqui.app.caches.LiquiCachesComponent;
import com.microee.traditex.liqui.app.components.LiquiRiskSettings;
import com.microee.traditex.liqui.app.components.PricingStreamService;
import com.microee.traditex.liqui.app.constrants.LiquiPrefixs;
import com.microee.traditex.liqui.app.core.conn.ConnectBootstrap;
import com.microee.traditex.liqui.app.core.conn.ConnectServiceMap;
import com.microee.traditex.liqui.app.core.hedg.HedgingService;
import com.microee.traditex.liqui.app.core.revoke.RevokeService;
import com.microee.traditex.liqui.app.mappers.SolrNextOrderTableMapper;
import com.microee.traditex.liqui.app.producer.LiquiKafkaProducer;
import com.microee.traditex.liqui.app.props.LiquisConfProps;
import com.microee.traditex.liqui.app.props.conf.HBiTexAccountConf;
import com.microee.traditex.liqui.app.service.DiskOrderDetailService;
import com.microee.traditex.liqui.app.service.SolrOrderTableService;
import com.microee.traditex.liqui.oem.OrderBook;
import com.microee.traditex.liqui.oem.models.SolrNextOrderTable;
import com.microee.traditex.liqui.oem.models.SolrOrderTable;

// 流动性甩单服务
@Service
public class SolrOrderService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrOrderService.class);
    private static ThreadPoolFactoryLow threadPool =
            ThreadPoolFactoryLow.newInstance("traditex-liqui-甩单线程池");

    private Retryer<Boolean> retryer = null;

    @Autowired
    private RedisHash redisHash;

    @Autowired
    private ConnectBootstrap setupConnectService;

    @Autowired
    private TradiTexHBiTexOrderClient hbiTexOrderClient;

    @Autowired
    private PricingStreamService pricingStreamService;

    @Autowired
    private RevokeService revokeService;

    @Autowired
    private SolrOrderTableService solrOrderTableService;

    @Autowired
    private ConnectServiceMap connectServiceMap;

    @Autowired
    private DiskOrderDetailService diskOrderDetailService;

    @Autowired
    private LiquiRiskSettings liquiConf;

    @Autowired
    private HedgingService hedgingService;

    @Autowired
    private LiquiCachesComponent liquiCaches;

    @Autowired
    private SolrNextOrderTableMapper solrNextOrderTableMapper;

    @Autowired
    private LiquiKafkaProducer kafkaProducer;

    @Resource
    private LiquisConfProps liquisConfProps;

    // 创建对冲订单
    public void createSolrOrder(String connid, VENDER vender, HBiTexOrderDetails orderDetails) {
        JSONObject linkJSONObject = revokeService.getDiskOrderById(orderDetails.getId());
        diskOrderDetailService.save(orderDetails);
        if (linkJSONObject == null) {
            return;
        }
        String diskClientOrderId = orderDetails.getClientOrderId();
        String symbol = orderDetails.getSymbol();
        String orderType = orderDetails.getType();
        String diskOrderId = orderDetails.getId();
        String filledAmount = orderDetails.getFieldAmount();
        String filledPrice = orderDetails.getPrice();
        String filledCashAmount = orderDetails.getFieldCashAmount();
        OrderSideEnum side = null;
        if (orderType.indexOf("sell") != -1) {
            // 挂的卖单成交, 甩买
            side = OrderSideEnum.BUY;
        } else if (orderType.indexOf("buy") != -1) {
            // 挂的买单成交, 甩卖
            side = OrderSideEnum.SELL;
        }
        //@formatter:off
        String solrClientOrderId = String.format("%s_%s_%s", diskClientOrderId, filledCashAmount.replace(".", "_"), filledAmount.replace(".", "_"));
        
        // oanda 做多或做空
        hedgingService.createOrder(diskClientOrderId, null, orderDetails.getFinishedAt(), side, new BigDecimal(filledCashAmount));
        SolrOrderTable solrOrder = solrProcess(solrClientOrderId, symbol, side, diskOrderId, filledAmount, filledPrice, filledCashAmount);
        //@formatter:on
        threadPool.pool().submit(() -> this.solrOrderRetryer(solrOrder));
    }

    //@formatter:off
    // linkJSONObject={
    // "source":{"symbol":"btcusdt","side":"BUY","amount":0.001093,"price":10770.7,"vender":"HBiTex"},
    // "config":{"resthost":"http://***********************","connid":"gYRmoQnQ6Gx"},
    // "target":{"USDT_USD_RATE":"1.0007","symbol":"BTCJPY","side":"BUY","amount":0.001093,"price":1138483.88084972,"USD_JPY_RATE":"105.628"},
    // "distOrderParam":{"symbol":"btcjpy","side":"BUY","amount":"0.0010","pick":2,"price":"1138483.8808","clientOrderId":"5f730d1864895900010999ce","type":"buy-limit"},
    // "diskOrderResult":{"symbol":"btcjpy","match-id":100002461177,"role":"taker","filled-amount":"0.001","price":"1138442.429","filled-cash-amount":"1138.442429","order-state":"filled","order-id":125335618896864,"order-type":"buy-limit","unfilled-amount":"0","client-order-id":"5f730d1864895900010999ce"}
    // }
    //@formatter:on
    // 创建对冲订单
    public void createSolrOrder(String connid, VENDER vender, JSONObject diskOrderResult) {
        String diskResultOrderId = diskOrderResult.getLong("order-id") + "";
        if (!diskOrderDetailService.save(diskOrderResult)) {
            return;
        }
        String diskClientOrderId = diskOrderResult.getString("client-order-id");
        String diskMatchId = diskOrderResult.getLong("match-id") + "";
        String solrClientOrderId = String.format("%s_%s", diskClientOrderId, diskMatchId);
        String symbol = diskOrderResult.getString("symbol");
        String orderType = diskOrderResult.getString("order-type");
        String filledAmount = diskOrderResult.getString("filled-amount");
        String filledPrice = diskOrderResult.getString("price");
        String filledCashAmount = diskOrderResult.getString("filled-cash-amount");
        OrderSideEnum side = null;
        if (orderType.indexOf("sell") != -1) {
            // 挂的卖单成交, 甩买
            side = OrderSideEnum.BUY;
        } else if (orderType.indexOf("buy") != -1) {
            // 挂的买单成交, 甩卖
            side = OrderSideEnum.SELL;
        }
        // oanda 做多或做空
        hedgingService.createOrder(diskClientOrderId, diskMatchId, Instant.now().toEpochMilli(),
                side, new BigDecimal(filledCashAmount));
        //@formatter:off
        SolrOrderTable solrOrder = solrProcess(solrClientOrderId, symbol, side, diskResultOrderId, filledAmount, filledPrice, filledCashAmount);
        //@formatter:on
        threadPool.pool().submit(() -> this.solrOrderRetryer(solrOrder));
    }

    // 对冲时需考虑合单或拆单
    private SolrOrderTable solrProcess(String solrClientOrderId, String diskSymbol,
            OrderSideEnum solrSide, String diskResultOrderId, String filledAmount,
            String filledPrice, String filledCashAmount) {
        if (!diskSymbol.equals("btcjpy")) {
            LOGGER.error("流动性摆盘暂不支持的交易对: diskSymbol={}", diskSymbol);
            throw new RuntimeException("流动性摆盘暂不支持的交易对`" + diskSymbol + "`");
        }
        // 需考虑最小下单量, 如果成交量不足甩单最小下单量则"攒头寸", 攒够了在甩
        Integer solrBuyPrec = liquiConf.getSolrBuyPrecson(); // 读取配置
        Integer solrSellPrec = liquiConf.getSolrSellPrecson();
        BigDecimal USDT_USD_RATE = liquiConf.getUSDTUSDRate();
        BigDecimal USD_JPY_RATE =
                pricingStreamService.readPrice(solrSide, SymbolEnums.USDJPY.name());
        BigDecimal solrAmount = new BigDecimal(filledAmount);
        BigDecimal solrMoney =
                new BigDecimal(filledCashAmount).divide(USD_JPY_RATE, 18, RoundingMode.DOWN)
                        .divide(USDT_USD_RATE, 18, RoundingMode.DOWN);
        String solrTicker = "btcusdt";
        String solrType = solrSide.name().equals("BUY") ? "buy-market" : "sell-market";
        BigDecimal solrOrderAmount = null;
        Integer solrAmountPrec = null;
        // BigDecimal solrOrderMoney = null;
        if (solrType.equals("buy-market")) {
            // 市价买单数量为订单交易金额
            solrOrderAmount = solrMoney.setScale(solrBuyPrec, BigDecimal.ROUND_DOWN);
            solrAmountPrec = solrBuyPrec;
        } else {
            solrOrderAmount = solrAmount.setScale(solrSellPrec, BigDecimal.ROUND_DOWN);
            solrAmountPrec = solrSellPrec;
        }
        SolrOrderTable result = new SolrOrderTable();
        result.setSolrClientOrderId(solrClientOrderId);
        result.setDiskResultOrderId(diskResultOrderId);
        result.setDiskSymbol(diskSymbol);
        result.setVender(VENDER.HBiTex.code);
        result.setSolrSymbol(solrTicker);
        result.setSolrSide(solrSide.name());
        result.setSolrAmount(solrAmount);
        result.setSolrPrice(null);
        result.setSolrOrderAmount(solrOrderAmount);
        result.setSolrOrderPrice(null);
        result.setSolrOrderType(solrType);
        result.setSolrPricePrec(null);
        result.setSolrAmountPrec(solrAmountPrec);
        result.setUsdCurrencyPricing(USD_JPY_RATE);
        result.setUsdtUsdRate(USDT_USD_RATE);
        // 进行一个预成交计算, 拆单
        this.calculateSolrOrderByOrderbook(solrSide, result);
        return result;
    }

    // side=BUY, solrOrderAmount=17563.5319, currentSolrAmount=44839.3587840, nextSolrOrderAmount=0,
    // pick1Price=13033.0, scale=0.01, solrOrderClientId=5f95281cdd7317a99a631e86_100002461189,
    // orderBookId=5f95292cdd7317a6452dd45d
    // side=SELL, solrOrderAmount=10.1642, currentSolrAmount=12.000267, nextSolrOrderAmount=0,
    // pick1Price=13035.7, scale=0.01, solrOrderClientId=5f95281cdd7317a99a631e87_100002461189,
    // orderBookId=5f9529a2dd7317a6452dd4d3
    // 进行一个预成交计算，计算这笔对冲金额或数量，可能会吃到对冲盘口的什么价位
    // 以1档为计算基准，预成交时，超过比例的部分不进行对冲，待2s后再重新发起二次对冲，直至对冲完毕
    // 根据orderbook决定当前订单数量吃几档
    public void calculateSolrOrderByOrderbook(OrderSideEnum side, SolrOrderTable solrOrder) {

        OrderBook orderbook = liquiCaches.readOrderBookRealTime();
        solrOrder.setOrderBookId(orderbook.orderBookId);
        JSONArray pickItems = side.equals(OrderSideEnum.SELL) ? orderbook.bids : orderbook.asks;
        BigDecimal pick1Price = side.equals(OrderSideEnum.SELL)
                ? liquiCaches.readPick1Price(OrderSideEnum.BUY, orderbook)
                : liquiCaches.readPick1Price(OrderSideEnum.SELL, orderbook);
        BigDecimal scale = side.equals(OrderSideEnum.SELL) ? liquiConf.getSolrPriceScaleMin()
                : liquiConf.getSolrPriceScaleMax();
        BigDecimal solrOrderAmount = solrOrder.getSolrOrderAmount();
        BigDecimal currentSolrAmount = BigDecimal.ZERO;
        BigDecimal nextSolrOrderAmount = BigDecimal.ZERO;

        BigDecimal currentTradeMoneyTotal = BigDecimal.ZERO;
        for (int i = 0; i < pickItems.length(); i++) {
            // 卖盘1档最便宜, 越来越贵
            // 买盘1档最便贵, 越来越贱
            JSONArray currentPick = pickItems.getJSONArray(i);
            BigDecimal currentDepthPrice = BigDecimal.valueOf(currentPick.getDouble(0));
            BigDecimal currentDepthAmount = BigDecimal.valueOf(currentPick.getDouble(1));
            BigDecimal currentTradeMoney = side.equals(OrderSideEnum.SELL) ? currentDepthAmount
                    : currentDepthPrice.multiply(currentDepthAmount);
            if (currentTradeMoneyTotal.compareTo(solrOrderAmount) >= 0) {
                break;
            }
            // 如果当前档位价格大于1档且超过了配置的比例上限则不累加
            if (i == 0) {
                // 1档交易量直接加进来
                currentTradeMoneyTotal = currentTradeMoneyTotal.add(currentTradeMoney);
                continue;
            }
            // 当前档位和1档价格的百分比
            BigDecimal currentSubRateThenPick1 = BigDecimal.ZERO;
            if (side.equals(OrderSideEnum.SELL)) {
                currentSubRateThenPick1 = pick1Price.subtract(currentDepthPrice).divide(pick1Price,
                        18, RoundingMode.DOWN);
            } else {
                currentSubRateThenPick1 = currentDepthPrice.subtract(pick1Price).divide(pick1Price,
                        18, RoundingMode.DOWN);
            }
            // 和配置的比率比较，如果小于配置的比率: 则累加
            if (currentSubRateThenPick1.compareTo(scale) > 0) {
                break;
            }
            currentTradeMoneyTotal = currentTradeMoneyTotal.add(currentTradeMoney);
        }

        if (currentTradeMoneyTotal.compareTo(solrOrderAmount) <= 0) {
            // 下一次甩的数量
            nextSolrOrderAmount = solrOrderAmount.subtract(currentTradeMoneyTotal);
            currentSolrAmount = currentTradeMoneyTotal;
        } else {
            currentSolrAmount = solrOrderAmount;
        }
        solrOrder.setSolrOrderAmount(currentSolrAmount);
        solrOrder.setSolrAmount(BigDecimal.ZERO);
        LOGGER.info(
                "进行一个预成交计算: side={}, solrOrderAmount={}, currentSolrAmount={}, nextSolrOrderAmount={}, pick1Price={}, scale={}, solrOrderClientId={}, orderBookId={}",
                side.name(), solrOrderAmount, currentSolrAmount, nextSolrOrderAmount, pick1Price,
                scale, solrOrder.getSolrClientOrderId(), orderbook.orderBookId);
        if (nextSolrOrderAmount.compareTo(BigDecimal.ZERO) == 1) {
            SolrNextOrderTable nextOrder = new SolrNextOrderTable();
            nextOrder.setNextSolrId(UUIDObject.get().toString());
            nextOrder.setSolrClientOrderId(solrOrder.getSolrClientOrderId());
            nextOrder.setSolrSymbol(solrOrder.getSolrSymbol());
            nextOrder.setSolrSide(side.name());
            nextOrder.setSolrOrderType(solrOrder.getSolrOrderType());
            nextOrder.setNextSolrQuantity(nextSolrOrderAmount);
            nextOrder.setDiskResultOrderId(solrOrder.getDiskResultOrderId());
            nextOrder.setDiskSymbol(solrOrder.getDiskSymbol());
            nextOrder.setCreatedAt(new Date(Instant.now().toEpochMilli()));
            this.save(nextOrder);
        }
    }

    // 保存下一个将要甩的订单
    public void save(SolrNextOrderTable nextOrder) {
        solrNextOrderTableMapper.insertSelective(nextOrder);
    }

    @PostConstruct
    public void init() {

    }

    @Override
    public void afterPropertiesSet() {
        retryer = RetryerBuilder.<Boolean>newBuilder().retryIfException() // 设置异常重试
                .retryIfResult(Predicates.equalTo(true)) // call方法返回true重试
                .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS)) // 设置3秒后重试
                // 设置重试次数超过将出异常, 最多重试次, 超过3此后报警
                .withStopStrategy(StopStrategies.stopAfterAttempt(150)).build();
    }

    public void solrOrderRetryer(SolrOrderTable solrOrder) {
        try {
            retryer.call(() -> {
                //@formatter:off
                String connid = setupConnectService.getConnidSolrConnid();
                HBiTexAccountConf accountConf = connectServiceMap.getAccountByConnId(connid, new TypeReference<HBiTexAccountConf>() {});
                String resthost = accountConf.getResthost();
                if (connid == null) {
                    return true;
                }
                solrOrder.setSolrAccount(accountConf.getUid());
                String clientOrderId = solrOrder.getSolrClientOrderId();
                String orderType = solrOrder.getSolrOrderType();
                String symbol = solrOrder.getSolrSymbol();
                String amount = solrOrder.getSolrOrderAmount().setScale(solrOrder.getSolrAmountPrec(), BigDecimal.ROUND_DOWN).toPlainString();
                String side = solrOrder.getSolrSide();
                HBiTexHttpResult<String> solrOrderResult = null;
                try {
                    solrOrder.setCreatedAt(new Date(Instant.now().toEpochMilli()));
                    solrOrderResult = hbiTexOrderClient.create(connid, clientOrderId, orderType, symbol, Strings.EMPTY, amount, side, resthost).getData();
                } catch (Exception e) {
                    LOGGER.error("甩单异常重试: solrOrder={}, solrOrderResult={}", HttpAssets.toJsonString(solrOrder), HttpAssets.toJsonString(solrOrderResult));
                    this.solrRetryCounter(clientOrderId, solrOrder);
                    return true;
                }
                String solrResultOrderId = null;
                if (solrOrderResult == null || solrOrderResult.getData() == null) {
                    if (solrOrderResult == null || !solrOrderResult.getErrCode().equals("invalid-client-order-id")) {
                        String _message = String.format("甩单失败重试报警: solrOrder=%s, solrOrderResult=%s", HttpAssets.toJsonString(solrOrder), HttpAssets.toJsonString(solrOrderResult));
                        LOGGER.error(_message);
                        kafkaProducer.sendMessage(_message);
                        this.solrRetryCounter(clientOrderId, solrOrder);
                        return true;
                    } else {
                        // 订单号重复
                        solrResultOrderId = hbiTexOrderClient.queryOrder(connid, resthost, clientOrderId).getData().getId();
                        LOGGER.info("订单号重复甩单成功, 根据客户端订单号查询订单: clientOrderId={}, solrResultOrderId={}, solrOrderResult={}", clientOrderId,
                                solrResultOrderId, HttpAssets.toJsonString(solrOrderResult));
                    }
                } else {
                    solrResultOrderId = solrOrderResult.getData();
                    LOGGER.info("甩单成功: solrOrderResult={}", HttpAssets.toJsonString(solrOrderResult));
                }
                try {
                    solrOrder.setSolrResultOrderId(solrResultOrderId);
                    solrOrder.setSolrOsst(0);
                    solrOrderTableService.save(solrOrder);
                } catch (Exception e) {
                    LOGGER.error("保存甩单信息异常: solrResultOrderId={}, errorMessage={}", solrResultOrderId, e.getMessage(), e);
                    return false;
                }
                redisHash.hdel(LiquiPrefixs.SOLR_ORDER_RETRY_COUNT_PREFIX, clientOrderId);
                return false;
                //@formatter:on
            });
        } catch (ExecutionException e) {
            LOGGER.error("甩单出现异常: errorMessage={}", e.getMessage(), e);
        } catch (RetryException e) {
            LOGGER.error("甩单重试异常: errorMessage={}", e.getMessage(), e);
        }
    }

    // 甩单重试次数+1
    private void solrRetryCounter(String clientOrderId, SolrOrderTable solrOrder) {
        long solrRetryCount =
                redisHash.hincr(LiquiPrefixs.SOLR_ORDER_RETRY_COUNT_PREFIX, clientOrderId, 1l);
        if (solrRetryCount > 3) {
            String _message = String.format("报警:甩单失败超过3次: clientOrderId=%s, solrOrder=%s",
                    clientOrderId, HttpAssets.toJsonString(solrOrder));
            LOGGER.error(_message);
            kafkaProducer.sendMessage(_message);
        }
    }

}


