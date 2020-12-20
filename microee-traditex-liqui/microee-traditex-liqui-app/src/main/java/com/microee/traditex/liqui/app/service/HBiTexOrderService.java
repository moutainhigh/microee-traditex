package com.microee.traditex.liqui.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.traditex.inbox.rmi.TradiTexHBiTexOrderClient;
import com.microee.traditex.liqui.app.core.conn.ConnectServiceMap;
import com.microee.traditex.liqui.app.props.conf.HBiTexAccountConf;

@Service
public class HBiTexOrderService {

    @Autowired
    private ConnectServiceMap connectServiceMap;
    
    @Autowired
    private TradiTexHBiTexOrderClient hbiTexOrderClient;
    
    public void queryOrderByClientOrderId(String connid, String clientOrderId) {

        HBiTexAccountConf accountConf = connectServiceMap.getAccountByConnId(connid,
                new TypeReference<HBiTexAccountConf>() {});
        hbiTexOrderClient.details(connid, accountConf.getResthost(), clientOrderId);
    }
    
}
