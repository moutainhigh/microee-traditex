package com.microee.traditex.liqui.app.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.microee.plugin.response.R;
import com.microee.traditex.liqui.app.core.conn.ConnectBootstrap;
import com.microee.traditex.liqui.app.props.conf.Conf;

@RestController
@RequestMapping("/conn")
public class LiquiConnectorRestful {

    @Autowired
    private ConnectBootstrap connectBootstrap;

    // 新建 orderbook 连接, 返回连接id
    @RequestMapping(value = "/orderbook/setup", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> orderbook(@RequestBody Conf conf) {
        return R.ok(connectBootstrap.setupOrderBookConnect(conf)); 
    }
    
    // 新建 Oanda 连接, 返回连接id
    @RequestMapping(value = "/pricing-stream/setup", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> pricingStream(@RequestBody Conf conf, @RequestParam("instruments") String[] instruments) {
        return R.ok(connectBootstrap.setupOandaConnect(conf, instruments)); 
    }
    
    // 新建摆盘账户资产及订单连接, 返回连接id
    @RequestMapping(value = "/disk/setup", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> diskSetup(@RequestBody Conf conf) {
        return R.ok(connectBootstrap.setupDiskAccountConnect(conf)); 
    }
    
    // 新建甩单账户资产及订单连接, 返回连接id
    @RequestMapping(value = "/solr/setup", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> solrSetup(@RequestBody Conf conf) {
        return R.ok(connectBootstrap.setupSolrAccountConnect(conf)); 
    }

}
