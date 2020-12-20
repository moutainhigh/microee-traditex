package com.microee.traditex.liqui.oem.rmi;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.microee.plugin.response.R;

public interface ILiquiDiskRMi {
 // ### 盘口挂单
    @RequestMapping(value = "/orders", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> orders();

    // ### 盘口挂单id
    @RequestMapping(value = "/orders-ids", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> orderIds() ;
    
    // ### 清除盘口挂单锁
    @RequestMapping(value = "/remove-orders-lock-key", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> removeOrderLockKey();
    
    // ### 查看撤单计数排名
    @RequestMapping(value = "/view-revoke-count-score", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Map<Object, Double>> viewRevokeCountScore() ;
}
