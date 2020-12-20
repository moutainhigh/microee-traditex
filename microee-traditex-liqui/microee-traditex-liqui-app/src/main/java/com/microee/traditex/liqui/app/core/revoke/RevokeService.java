package com.microee.traditex.liqui.app.core.revoke;

import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
import com.google.common.collect.Lists;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.response.R;
import com.microee.plugin.thread.ThreadPoolFactoryLow;
import com.microee.stacks.redis.support.Redis;
import com.microee.stacks.redis.support.RedisHash;
import com.microee.stacks.redis.support.RedisZSet;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.inbox.oem.hbitex.vo.HBiTexOrderDetails;
import com.microee.traditex.inbox.rmi.TradiTexHBiTexOrderClient;
import com.microee.traditex.liqui.app.components.LiquiRiskSettings;
import com.microee.traditex.liqui.app.constrants.LiquiPrefixs;
import com.microee.traditex.liqui.app.core.conn.ConnectBootstrap;
import com.microee.traditex.liqui.app.core.conn.ConnectServiceMap;
import com.microee.traditex.liqui.app.core.disk.DiskTableService;
import com.microee.traditex.liqui.app.core.solr.SolrOrderService;
import com.microee.traditex.liqui.app.producer.RedisProducer;
import com.microee.traditex.liqui.app.props.LiquisConfProps;
import com.microee.traditex.liqui.app.props.conf.HBiTexAccountConf;
import com.microee.traditex.liqui.oem.models.LiquiRiskStrategySettings;

// 流动性摆盘撤单服务
@Component
public class RevokeService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevokeService.class);
    private static ThreadPoolFactoryLow threadPool =
            ThreadPoolFactoryLow.newInstance("traditex-liqui-撤单延迟队列线程池");

    public static final int DELAY_REVOKE_ORDER_TIME_MS = 1000; // 50 毫秒后执行撤单
    private Retryer<Boolean> retryer = null;

    @Autowired
    private LiquisConfProps liquisConfProps;

    @Autowired
    private RedisHash redisHash;

    @Autowired
    private RedisZSet redisZSet;

    @Autowired
    private Redis redis;

    @Autowired
    private TradiTexHBiTexOrderClient hbiTexOrderClient;

    @Autowired
    private ConnectBootstrap setupConnectService;

    @Autowired
    private SolrOrderService solrOrderService;

    @Autowired
    private ConnectServiceMap connectServiceMap;

    @Autowired
    private DiskTableService diskTableService;

    @Resource
    private LiquiRiskSettings liquiConf;

    private final DelayQueue<DelayedRevokeItem<RevokeService>> delayRevokeQueue =
            new DelayQueue<>();

    @PostConstruct
    public void init() {

    }

    @Override
    public void afterPropertiesSet() {
        retryer = RetryerBuilder.<Boolean>newBuilder().retryIfException() // 设置异常重试
                .retryIfResult(Predicates.equalTo(true)) // call方法返回true重试
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS)) // 撤单重试间隔
                // 设置重试次数 超过将出异常, 最多撤 150000 次
                .withStopStrategy(StopStrategies.stopAfterAttempt(150000)).build();
        List<String> orderids = this.getPendingOrdersIds();
        if (orderids.size() > 0) {
            this.add(this);
        }
        if (liquisConfProps.getRevokeEnable()) {
            this.startupRevoke();
        }
    }

    // 启动延迟撤单
    public void startupRevoke() {
        threadPool.pool().submit(() -> {
            try {
                while (true) {
                    DelayedRevokeItem<RevokeService> delayRevoke = delayRevokeQueue.take();
                    delayRevoke.item.revokeRetryer();
                }
            } catch (InterruptedException e) {
                LOGGER.error("启动撤单延迟队列异常: delayQueueSize={}, erroeMessage={}",
                        delayRevokeQueue.size(), e.getMessage(), e);
            }
        });
    }

    // 将撤单队列放入延迟队列等待撤单
    public RevokeService add(RevokeService revoke) {
        delayRevokeQueue.put(
                new DelayedRevokeItem<>(revoke, DELAY_REVOKE_ORDER_TIME_MS, TimeUnit.MILLISECONDS));
        return this;
    }

    // 进入 pending, 进入该队列的会延迟撤单
    public void pending(String connid, VENDER vender, String newOrderId, String data) {
        redisHash.hset(LiquiPrefixs.DISK_ORDER_PREFIX, newOrderId, data);
    }

    // 根据订单ID查询盘口挂单
    public JSONObject getDiskOrderById(String newOrderId) {
        Object data = redisHash.hget(LiquiPrefixs.DISK_ORDER_PREFIX, newOrderId);
        if (data != null) {
            return new JSONObject(data.toString());
        }
        return null;
    }

    // 查看盘口挂单列表
    public Map<Object, Object> getPendingOrders() {
        return redisHash.hmget(LiquiPrefixs.DISK_ORDER_PREFIX);
    }

    // 查看盘口挂单列表id
    public List<String> getPendingOrdersIds() {
        Map<Object, Object> orders = this.getPendingOrders();
        return orders.keySet().parallelStream().map(m -> m.toString()).collect(Collectors.toList());
    }

    // 清除盘口挂单队列
    public Boolean removeOrderLockKey() {
        redis.del(LiquiPrefixs.DISK_ORDER_PREFIX);
        redis.del(LiquiPrefixs.DISK_ORDER_REVOKE_SAORE_PREFIX);
        this.diskTableService.unlock();
        diskTableService.unlockPlacingOrder();
        LOGGER.warn("清除盘口挂单: isLocked={}, placingOrder: {}", this.diskTableService.islock(),
                diskTableService.isPlacingOrder());
        return true;
    }

    // 删除一个盘口挂单
    public void removeADiskOrder(String diskOrderId) {
        redisHash.hdel(LiquiPrefixs.DISK_ORDER_PREFIX, diskOrderId);
        redisZSet.remove(LiquiPrefixs.DISK_ORDER_REVOKE_SAORE_PREFIX, diskOrderId);
        String key = LiquiPrefixs.DISK_ORDER_PREFIX;
        if (redisHash.sizeOf(key) == 0) {
            this.diskTableService.unlock();
        }
        LOGGER.warn("盘口挂单锁: isLocked={}, placingOrder: {}", this.diskTableService.islock(),
                diskTableService.isPlacingOrder());
    }

    // 递增撤单计数
    public void incrRevokeCountScore(Object[] diskOrderId) {
        redisZSet.incr(LiquiPrefixs.DISK_ORDER_REVOKE_SAORE_PREFIX, diskOrderId);
    }

    // 查看撤单计数排名
    public Map<Object, Double> viewRevokeCountScore() {
        return redisZSet.score(LiquiPrefixs.DISK_ORDER_REVOKE_SAORE_PREFIX, 600);
    }

    @Autowired
    private RedisProducer redisProducer;

    public void revokeRetryer() {
        try {
            retryer.call(() -> {
                List<String> orderids = this.getPendingOrdersIds();
                if (orderids.size() == 0) {
                    return false;
                }
                String connid = setupConnectService.getConnidDiskConnid();
                HBiTexAccountConf accountConf = connectServiceMap.getAccountByConnId(connid,
                        new TypeReference<HBiTexAccountConf>() {});
                if (accountConf == null) {
                    return true;
                }
                List<List<String>> smallerLists = Lists.partition(orderids, 45);
                for (int i = 0; i < smallerLists.size(); i++) {
                    String[] orderIds =
                            smallerLists.get(i).toArray(new String[smallerLists.get(i).size()]);
                    R<?> revokeResult = null;
                    try {
                        revokeResult = this.hbiTexOrderClient.revoke(connid,
                                accountConf.getResthost(), orderIds);
                    } catch (Exception e) {
                        LOGGER.error("流动性摆盘撤单状态错误: revokeResult={}, errorMessage={}",
                                HttpAssets.toJsonString(revokeResult), e.getMessage(), e);
                    }
                    this.incrRevokeCountScore(orderIds); // 撤单排名加1
                }
                if (this.getPendingOrdersIds().size() > 0) {
                    redisProducer.broadcaseDiskOrders(this.getPendingOrders());
                    redisProducer.broadcaseRevokeOrderCount(this.viewRevokeCountScore());
                    return true; // 需要重试返回true
                }
                return false; // 不需要重试
            });
        } catch (ExecutionException e) {
            LOGGER.error("流动性摆盘撤单重试异常: errorMessage={}", e.getMessage());
        } catch (RetryException e) {
            LOGGER.error("流动性摆盘撤单重试异常: errorMessage={}", e.getMessage());
        }
    }

    public Boolean scanRevokeDiskOrderById(String connid, String diskOrderId) {
        LOGGER.info("扫描撤单, connId: {}, orderId: {}", connid, diskOrderId);
        JSONObject diskOrder = this.getDiskOrderById(diskOrderId);
        if (diskOrder == null) {
            this.removeADiskOrder(diskOrderId);
            return true;
        }
        R<HBiTexOrderDetails> result = null;
        try {
            result = hbiTexOrderClient.details(connid,
                    diskOrder.getJSONObject("config").getString("resthost"), diskOrderId);
        } catch (Exception e) {
            LOGGER.warn("扫描撤单调用broker出现了错误, connId: {}, orderId: {}", connid, diskOrderId, e);
            redisProducer.broadcaseHttpNetwork(
                    new JSONObject().put("title", "挂单-扫描, 查询订单详情异常").put("diskOrderId", diskOrderId)
                            .put("diskOrder", diskOrder).put("errorMessage", e.getMessage()));
            return true;
        }

        if (!R.isok(result.getCode())) {
            LOGGER.warn("扫描撤单调用broker失败, connId: {}, orderId: {}, code: {}, msg: {}", connid,
                    diskOrderId, result.getCode(), result.getMessage());
            return true;
        }


        HBiTexOrderDetails orderDetails = result.getData();
        if (orderDetails.getState().equals("submitted")) {
            return true;
        }

        if (orderDetails.getState().equals("canceled")) {
            LOGGER.info("订单扫描撤单成功: vender={}, connid={}, diskOrderId={}", VENDER.HBiTex.name(),
                    connid, orderDetails.getId());
            this.removeADiskOrder(orderDetails.getId());
            return true;
        }

        if (orderDetails.getState().equals("partial-canceled")) {
            LOGGER.info("订单扫描部分撤单: vender={}, connid={}, diskOrderId={}", VENDER.HBiTex.name(),
                    connid, orderDetails.getId());
            this.removeADiskOrder(orderDetails.getId());
            return true;
        }

        // 成交或者部分成交的需要判断盘面是否关闭
        LiquiRiskStrategySettings settings =
                liquiConf.getCachedStrategySettings(LiquiPrefixs.SYMBOL_BTC_USDT);
        if (settings.isFuse()) {
            LOGGER.info("挂单状态变化挂单全部成交/部分成交, 因盘口关闭不进行对冲: vender={}, connid={}, orderMessage={}",
                    VENDER.HBiTex.name(), connid, orderDetails.getId());
            if (orderDetails.getState().equals("filled")) {
                this.removeADiskOrder(orderDetails.getId());
            }
            return true;
        }

        if (orderDetails.getState().equals("filled")) {
            LOGGER.info("订单扫描全部成交: vender={}, connid={}, diskOrderId={}", VENDER.HBiTex.name(),
                    connid, orderDetails.getId());
            solrOrderService.createSolrOrder(connid, VENDER.HBiTex, orderDetails);
            this.removeADiskOrder(orderDetails.getId());
            return true;
        }

        if (orderDetails.getState().equals("partial-filled")) {
            LOGGER.info("订单扫描部分成交: vender={}, connid={}, diskOrderId={}", VENDER.HBiTex.name(),
                    connid, orderDetails.getId());
            // revokeService.removeADiskOrder(orderDetails.getId());
            solrOrderService.createSolrOrder(connid, VENDER.HBiTex, orderDetails);
            return true;
        }
        return false;
    }

}


class DelayedRevokeItem<T> implements Delayed {

    private long time; /* 触发时间 */
    public T item;

    public DelayedRevokeItem(T item, long time, TimeUnit unit) {
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
        DelayedRevokeItem<?> item = (DelayedRevokeItem) o;
        long diff = this.time - item.time;
        if (diff <= 0) {// 改成>=会造成问题
            return -1;
        }
        return 1;
    }

}

