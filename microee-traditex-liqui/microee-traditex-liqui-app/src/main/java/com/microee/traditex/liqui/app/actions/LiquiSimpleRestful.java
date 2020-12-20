package com.microee.traditex.liqui.app.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.response.R;
import com.microee.traditex.liqui.app.props.LiquisConfProps;
import com.microee.traditex.liqui.app.props.ProxyProps;
import com.microee.traditex.liqui.app.props.conf.HBiTexAccountConf;
import com.microee.traditex.liqui.app.props.conf.OandaStreamConf;
import com.microee.traditex.liqui.app.props.conf.OrderBookHBiTexConf;

@RestController
@RequestMapping("/simples")
public class LiquiSimpleRestful {

    @Autowired
    private LiquisConfProps liquisConfProps;
    
    @Autowired
    private ProxyProps proxyProps;

    @RequestMapping(value = "/getConfs", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> getConfs() {
        return R.ok(liquisConfProps.getConfs());
    }

    @RequestMapping(value = "/proxyProps", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> proxyProps() {
        return R.ok(proxyProps.getServer() + ":" + proxyProps.getPort());
    }

    @RequestMapping(value = "/orderBookConf", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> orderBookConf() {
        return R.ok(liquisConfProps.getConfs().get(0).getOrderbook().map(new TypeReference<OrderBookHBiTexConf>() {}));
    }

    @RequestMapping(value = "/accountDiskConf", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> accountConf() {
        return R.ok(liquisConfProps.getConfs().get(0).getDisk().map(new TypeReference<HBiTexAccountConf>() {})); 
    }

    @RequestMapping(value = "/accountSolrConf", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> accountSolrConf() {
        return R.ok(liquisConfProps.getConfs().get(0).getSolr().map(new TypeReference<HBiTexAccountConf>() {})); 
    }

    @RequestMapping(value = "/hedgingConf", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> hedgingConf() {
        return R.ok(liquisConfProps.getConfs().get(0).getHedging().map(new TypeReference<OandaStreamConf>() {})); 
    }
    
}
