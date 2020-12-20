package com.microee.traditex.liqui.app.core.revoke;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.microee.traditex.liqui.app.core.conn.ConnectBootstrap;
import com.microee.traditex.liqui.app.props.LiquisConfProps;

// 定时扫描订单状态并主动查询并清除已经撤掉的订单
@Component
public class RevokeScanService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevokeScanService.class);
    
    private static final int REVOKE_SCAN_DURATION = 3;
    
    @Autowired
    private RevokeService revokeService;

    @Autowired
    private ConnectBootstrap connectService;

    @Autowired
    private LiquisConfProps liquisConfProps;
    
    @PostConstruct
    public void init() {
        
    }

    @Scheduled(initialDelay = 5 * 1_000L, fixedDelay = REVOKE_SCAN_DURATION * 1_000)
    public void scan() {
        if (!liquisConfProps.getRevokeEnable()) {
            return;
        }
        String connid = connectService.getConnidDiskConnid();
        if (connid == null) {
            return;
        }
        Map<Object, Double> revokeQueue = revokeService.viewRevokeCountScore();
        Map<Object, Double> revokeQueueManuall = new HashMap<>();
        for (Entry<Object, Double> entry : revokeQueue.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            if (entry.getValue() >= 1) {
                revokeQueueManuall.put(entry.getKey(), entry.getValue());
            }
        }
        if (revokeQueueManuall.size() > 0) {
            for (Entry<Object, Double> entry : revokeQueueManuall.entrySet()) {
                String diskOrderId = entry.getKey().toString();
                if (!revokeService.scanRevokeDiskOrderById(connid, diskOrderId)) {
                    LOGGER.warn("撤单扫描: orderId={}", diskOrderId);
                }
            }
        }
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
//        if (liquisConfProps.getRevokeEnable()) {
//            ThreadPoolFactoryScheduled.newInstance().pool().scheduleAtFixedRate(
//                    () -> this.scan(), 5, REVOKE_SCAN_DURATION, TimeUnit.SECONDS);
//        }
    }
    
}
