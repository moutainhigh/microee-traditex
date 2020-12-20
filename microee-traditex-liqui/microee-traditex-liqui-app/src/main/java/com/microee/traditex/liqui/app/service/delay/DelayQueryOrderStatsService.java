package com.microee.traditex.liqui.app.service.delay;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.thread.ThreadPoolFactoryLow;
import com.microee.traditex.inbox.oem.constrants.OrderSideEnum;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.inbox.oem.hbitex.vo.HBiTexOrderDetails;
import com.microee.traditex.inbox.rmi.TradiTexHBiTexOrderClient;
import com.microee.traditex.liqui.app.core.conn.ConnectServiceMap;
import com.microee.traditex.liqui.app.core.disk.DiskOrderParam;
import com.microee.traditex.liqui.app.core.revoke.RevokeService;
import com.microee.traditex.liqui.app.props.conf.HBiTexAccountConf;

// 对于下单超时的未知状态订单, 延迟查询订单状态, 如是挂单状态则加入撤单队列
@Component
public class DelayQueryOrderStatsService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayQueryOrderStatsService.class);
    private static ThreadPoolFactoryLow threadPool = ThreadPoolFactoryLow.newInstance("traditex-liqui-查询挂单状态延迟队列线程");
    private final Retryer<Boolean> queryRetryer = RetryerBuilder.<Boolean>newBuilder().retryIfException() // 设置异常重试
                                                .retryIfResult(Predicates.equalTo(true)) // call方法返回true重试
                                                .withWaitStrategy(WaitStrategies.fixedWait(DELAY_QUERY_ORDER_TIME_MS, TimeUnit.MILLISECONDS)) // 设置1秒后重试
                                                .withStopStrategy(StopStrategies.stopAfterAttempt(150)).build(); // 设置重试次数 超过将出异常, 最多查 15 次
    
    public static final int DELAY_QUERY_ORDER_TIME_MS = 1300; // 1300 毫秒后查询挂单
    
    @Autowired
    private TradiTexHBiTexOrderClient hbiTexOrderClient;

    @Autowired
    private ConnectServiceMap connectServiceMap;

    @Autowired
    private RevokeService liquiRevokeService;
    
    private final DelayQueue<DelayedQueryDiskOrderItem<QueryDiskOrderItem>> delayQueryQueue =
            new DelayQueue<>();

    public void delayQuery(String connid, String clientOrderId) {
        this.delayQueryQueue.put(new DelayedQueryDiskOrderItem<>(new QueryDiskOrderItem(connid, clientOrderId), DELAY_QUERY_ORDER_TIME_MS, TimeUnit.MILLISECONDS));
    }

    @Override
    public void afterPropertiesSet() {
        this.startupDelayQuery();
    }

    // 启动延迟撤单
    public void startupDelayQuery() {
        threadPool.pool().submit(() -> {
            LOGGER.info("启动延迟查询挂单状态队列: 延迟时间={} 毫秒", DELAY_QUERY_ORDER_TIME_MS);
            try {
                while (true) {
                    DelayedQueryDiskOrderItem<QueryDiskOrderItem> delayRevoke = delayQueryQueue.take();
                    this.queryRetryer(delayRevoke.item);
                }
            } catch (InterruptedException e) {
                LOGGER.error("启动延迟查询挂单状态队列异常: delayQueueSize={}, erroeMessage={}", delayQueryQueue.size(), e.getMessage(), e);
            }
        });
    }

    public void queryRetryer(QueryDiskOrderItem orderItem) {
        try {
            queryRetryer.call(() -> {    
                HBiTexAccountConf accountConf = connectServiceMap.getAccountByConnId(orderItem.getConnid(),
                        new TypeReference<HBiTexAccountConf>() {});
                try {
                    HBiTexOrderDetails diskOrderDetails = hbiTexOrderClient
                            .queryOrder(orderItem.getConnid(), accountConf.getResthost(), orderItem.getClientOrderId()).getData();
                    if (diskOrderDetails == null) {
                        // 没查到什么也不做
                        return true;
                    }
                    if (diskOrderDetails.getState().equals("submitted")) {
                        JSONObject linkJSONObject = new JSONObject();
                        String side = null;
                        if (diskOrderDetails.getType().indexOf("buy") != -1) {
                            side = OrderSideEnum.BUY.name();
                        }
                        if (diskOrderDetails.getType().indexOf("sell") != -1) {
                            side = OrderSideEnum.SELL.name();
                        }
                        String diskAccount = accountConf.getUid();
                        DiskOrderParam diskOrderParam = new DiskOrderParam(diskOrderDetails.getSymbol(), side, diskOrderDetails.getType(), diskOrderDetails.getAmount(), diskOrderDetails.getPrice(), diskOrderDetails.getClientOrderId());
                        linkJSONObject.put("distOrderParam", new JSONObject(HttpAssets.toJsonString(diskOrderParam)));
                        linkJSONObject.put("config", new JSONObject().put("resthost", accountConf.getResthost()).put("connid", orderItem.getConnid()).put("uid", diskAccount));
                        LOGGER.info("查询订单是否挂单状态如是挂单状态则加入撤单队列: connid={}, diskOrderDetails={}", orderItem.getConnid(), HttpAssets.toJsonString(diskOrderDetails));
                        liquiRevokeService.pending(orderItem.getConnid(), VENDER.HBiTex,
                                diskOrderDetails.getId(), linkJSONObject.toString()); // 放入撤单队列
                    }
                    return false; // 不需要重试
                } catch (Exception e) {
                    // 查询超时
                    LOGGER.error("根据客户端id查询挂单详情异常: connid={}, clientOrderId={}, errorMessage={}", orderItem.getConnid(), orderItem.getClass(), e.getMessage(), e);
                    return true; // 需要重试返回true
                }
            });
        } catch (ExecutionException e) {
            LOGGER.error("延迟队列查询挂单重试异常: errorMessage={}", e.getMessage());
        } catch (RetryException e) {
            LOGGER.error("延迟队列查询挂单重试异常: errorMessage={}", e.getMessage());
        }
    }
    
    public static class DelayedQueryDiskOrderItem<T> implements Delayed {

        private long time; /* 触发时间 */
        public T item;

        public DelayedQueryDiskOrderItem(T item, long time, TimeUnit unit) {
            this.item = item;
            this.time = System.currentTimeMillis() + (time > 0 ? unit.toMillis(time) : 0);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return time - System.currentTimeMillis();
        }

        @Override
        public int compareTo(Delayed o) {
            @SuppressWarnings("rawtypes")
            DelayedQueryDiskOrderItem<?> item = (DelayedQueryDiskOrderItem) o;
            long diff = this.time - item.time;
            if (diff <= 0) {// 改成>=会造成问题
                return -1;
            }
            return 1;
        }
        
    }
    
    public static class QueryDiskOrderItem {
        
        private String connid;
        private String clientOrderId;
        
        public QueryDiskOrderItem() {
            
        }

        public QueryDiskOrderItem(String connid, String clientOrderId) {
            super();
            this.connid = connid;
            this.clientOrderId = clientOrderId;
        }

        public String getConnid() {
            return connid;
        }

        public void setConnid(String connid) {
            this.connid = connid;
        }

        public String getClientOrderId() {
            return clientOrderId;
        }

        public void setClientOrderId(String clientOrderId) {
            this.clientOrderId = clientOrderId;
        }
        
    }
    
}
