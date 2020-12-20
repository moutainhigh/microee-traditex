package com.microee.traditex.liqui.app.components;

import javax.annotation.Resource;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.liqui.app.constrants.LiquiPrefixs;
import com.microee.traditex.liqui.app.core.conn.ConnectBootstrap;
import com.microee.traditex.liqui.app.core.revoke.RevokeService;
import com.microee.traditex.liqui.app.core.solr.SolrOrderService;
import com.microee.traditex.liqui.app.producer.LiquiKafkaProducer;
import com.microee.traditex.liqui.app.service.SolrOrderDetailService;
import com.microee.traditex.liqui.oem.models.LiquiRiskStrategySettings;

// 订单状态处理
@Component
public class OrderStatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderStatService.class);

    @Autowired
    private RevokeService revokeService;

    @Autowired
    private SolrOrderService solrOrderService;

    @Autowired
    private SolrOrderDetailService solrOrderDetailService;

    @Autowired
    private ConnectBootstrap setupConnectService;

    @Autowired
    private LiquiKafkaProducer kafkaProducer;

    @Resource
    private LiquiRiskSettings liquiConf;
    
    // {"symbol":"btcjpy","match-id":100000406018,"role":"taker","filled-amount":"0","price":"1147719.9425","filled-cash-amount":"0","order-state":"canceled","order-id":117127552925820,"order-type":"sell-limit","unfilled-amount":"0.001","client-order-id":"5f61c84364895900015fffb4"}
    // {"symbol":"btcjpy","match-id":100000410798,"role":"taker","filled-amount":"0.2","price":"1145339.6574","filled-cash-amount":"229067.93148","order-state":"filled","order-id":117127552926795,"order-type":"buy-limit","unfilled-amount":"0","client-order-id":"5f61dfff6489590001dac203"}
    // {"symbol":"btcjpy","match-id":100000412412,"role":"taker","filled-amount":"0","price":"1147043.0517","filled-cash-amount":"0","order-state":"submitted","order-id":117127552927006,"order-type":"sell-limit","unfilled-amount":"0.0028","client-order-id":"5f61e2716489590001781dcc"}
    public void process(JSONObject orderstate) {
        // 撤单成功, 从撤单队列删除对应key
        // 全部成交, 从撤单队列删除对应key
        // 部分成交, 从撤单队列找到对应的订单将成交金额减去成交部分
        // 部分撤掉, 
        // 将成交的部分在外汇期货市场做对冲
        VENDER vender = VENDER.get(orderstate.getString("vender"));
        String connid = orderstate.getString("connid");
        String connidSolr = setupConnectService.getConnidSolrConnid();
        String connidDisk = setupConnectService.getConnidDiskConnid();
        JSONObject orderDetails = orderstate.getJSONObject("message").getJSONObject("data");
        String orderId = orderDetails.getLong("order-id") + "";
        String orderStat = orderDetails.getString("order-state");
        LOGGER.info("收到订单状态变化: vender={}, connid={}, orderId={}, orderstate={}, orderDetails={}", vender, connid, orderId, orderStat, orderDetails.toString());
        if (orderStat.equals("submitted")) {
            if (connidDisk != null && connidDisk.equals(connid)) {
                return;
            }
            if (connidSolr != null && connidSolr.equals(connid)) {
                LOGGER.info("收到订单状态变化甩单成功: vender={}, connid={}, orderMessage={}", vender, connid, orderDetails.toString());
                return;
            }
        }
        if (orderStat.equals("canceled")) {
            if (connidDisk != null && connidDisk.equals(connid)) {
                revokeService.removeADiskOrder(orderId); 
                return;
            }
            if (connidSolr != null && connidSolr.equals(connid)) {
                String _message = String.format("报警:甩单状态变化市价单被取消,可能产生头寸,请手动平仓: vender=%s, connid=%s, orderMessage=%s", vender, connid, orderDetails.toString());
                kafkaProducer.sendMessage(_message);
                LOGGER.info(_message);
                return;
            }
        }
        if (orderStat.equals("partial-canceled")) {
            if (connidDisk != null && connidDisk.equals(connid)) {
                revokeService.removeADiskOrder(orderId);
                String _message = String.format("挂单状态变化挂单部分取消: vender=%s, connid=%s, orderMessage=%s", vender, connid, orderDetails.toString());
                LOGGER.info(_message);
                return;
            }
            if (connidSolr != null && connidSolr.equals(connid)) {
                String _message = String.format("甩单状态变化甩单部分撤销,可能产生头寸报警: vender=%s, connid=%s, orderMessage=%s", vender, connid, orderDetails.toString());
                kafkaProducer.sendMessage(_message);
                LOGGER.info(_message);
                solrOrderDetailService.save(connid, orderDetails);
                return;
            }
            return;
        }

        // 全部成交或者部分成交需要判断盘面是否关闭
        LiquiRiskStrategySettings settings = liquiConf.getCachedStrategySettings(LiquiPrefixs.SYMBOL_BTC_USDT);
        if (settings.isFuse()) {
            LOGGER.info("挂单状态变化挂单全部成交/部分成交, 因盘口关闭不进行对冲: vender={}, connid={}, orderMessage={}",
                    vender, connid, orderDetails);
            if (orderStat.equals("filled") && connidDisk != null && connidDisk.equals(connid)) {
                revokeService.removeADiskOrder(orderId);
            }
            return;
        }

        if (orderStat.equals("filled")) {
            if (connidDisk != null && connidDisk.equals(connid)) {
                LOGGER.info("挂单状态变化挂单全部成交: vender={}, connid={}, orderMessage={}", vender, connid, orderDetails.toString());
                solrOrderService.createSolrOrder(connid, vender, orderDetails);
                revokeService.removeADiskOrder(orderId);
                return;
            }
            if (connidSolr != null && connidSolr.equals(connid)) {
                LOGGER.info("甩单状态变化甩单全部成交: vender={}, connid={}, orderMessage={}", vender, connid, orderDetails.toString());
                solrOrderDetailService.save(connid, orderDetails);
                return;
            }
        }

        if (orderStat.equals("partial-filled")) {
            if (connidDisk != null && connidDisk.equals(connid)) {
                LOGGER.info("挂单状态变化挂单部分成交: vender={}, connid={}, orderMessage={}", vender, connid, orderDetails.toString());
                solrOrderService.createSolrOrder(connid, vender, orderDetails);
                return;
            }
            if (connidSolr != null && connidSolr.equals(connid)) {
                LOGGER.info("甩单状态变化甩单部分成交,可能产生头寸,请手动平仓: vender={}, connid={}, orderMessage={}", vender, connid, orderDetails.toString());
                return;
            }
        }
        LOGGER.warn("收到订单状态变化未处理: vender={}, connid={}, orderMessage={}", vender, connid, orderDetails.toString());
    }
    
}
