package com.microee.traditex.inbox.app.validator;

import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.microee.traditex.inbox.app.components.TradiTexConnectComponent;
import com.microee.traditex.inbox.oem.connector.ITridexTradFactory;
import com.microee.traditex.inbox.oem.connector.TradiTexConnection;
import com.microee.traditex.inbox.oem.constrants.ConnectStatus;
import com.microee.traditex.inbox.up.b2c2.B2C2Factory;
import com.microee.traditex.inbox.up.cumberland.CumberLandFactory;
import com.microee.traditex.inbox.up.hbitex.HBiTexTradFactory;
import com.microee.traditex.inbox.up.jumptrading.JumpTradingFactory;
import com.microee.traditex.inbox.up.oanda.OandaTradFactory;

@Component
public class RestValidator {

    @Autowired
    private TradiTexConnectComponent connectionComponent;
    
    public RestValidator connIdValid(String connid) {
        Assertions.assertThat(connectionComponent.hasKey(connid)).withFailMessage("%s 无效", connid).isTrue();
        return this;
    }

    public RestValidator connIdFun(String connid, TradiTexConnection<?> connection) {
        Assertions.assertThat(connection.getFactory()).withFailMessage("%s 连接未建立", connid).isNotNull();
        Assertions.assertThat(connection.getFactory().status().equals(ConnectStatus.ONLINE)).withFailMessage("%s 连接未打开, status=%s", connid, connection.getFactory().status()).isTrue();
        return this;
    }
    
    public RestValidator connIdIllegalHbiTex(String connid) {
        Assertions.assertThat(connectionComponent.get(connid).getFactory() instanceof HBiTexTradFactory).withFailMessage("%s 不匹配", connid).isTrue();
        return this;
    }
    
    public RestValidator connIdIllegalJumpTrading(String connid) {
        Assertions.assertThat(connectionComponent.get(connid).getFactory() instanceof JumpTradingFactory).withFailMessage("%s 不匹配", connid).isTrue();
        return this;
    }

    public RestValidator connIdIllegalCumberland(String connid) {
        Assertions.assertThat(connectionComponent.get(connid).getFactory() instanceof CumberLandFactory).withFailMessage("%s 不匹配", connid).isTrue();
        return this;
    }

    public RestValidator connIdIllegalB2C2(String connid) {
        Assertions.assertThat(connectionComponent.get(connid).getFactory() instanceof B2C2Factory).withFailMessage("%s 不匹配", connid).isTrue();
        return this;
    }
    
    public RestValidator connIdIllegalOanda(String connid) {
        Assertions.assertThat(connectionComponent.get(connid).getFactory() instanceof OandaTradFactory).withFailMessage("%s 不匹配", connid).isTrue();
        return this;
    }
    
    public RestValidator validateStaus(String connid, ITridexTradFactory factory) {
        if (factory != null) {
            // 只有这五种状态可以重连
            Boolean b = factory.status().equals(ConnectStatus.DAMAGED) || 
                    factory.status().equals(ConnectStatus.FAILED)|| 
                    factory.status().equals(ConnectStatus.TIMEOUT)|| 
                    factory.status().equals(ConnectStatus.CLOSED)|| 
                    factory.status().equals(ConnectStatus.UNKNOW);
            Assertions.assertThat(b).withFailMessage("%s 当前状态不允许连接, status=%s", connid, factory.status()).isTrue();
        }
        return this;
    }
    
}
