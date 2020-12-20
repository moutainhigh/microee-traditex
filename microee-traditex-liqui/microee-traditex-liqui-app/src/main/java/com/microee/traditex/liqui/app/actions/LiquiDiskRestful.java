package com.microee.traditex.liqui.app.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.microee.plugin.response.R;
import com.microee.traditex.liqui.app.components.LiquiRiskSettings;
import com.microee.traditex.liqui.app.core.disk.DiskTableService;
import com.microee.traditex.liqui.app.core.revoke.RevokeScanService;
import com.microee.traditex.liqui.app.core.revoke.RevokeService;
import com.microee.traditex.liqui.oem.enums.OrderBookSkipCodeEnum;
import com.microee.traditex.liqui.oem.models.LiquiRiskAlarmSettings;
import com.microee.traditex.liqui.oem.models.LiquiRiskStrategySettings;
import com.microee.traditex.liqui.oem.rmi.ILiquiDiskRMi;

@RestController
@RequestMapping("/disk")
public class LiquiDiskRestful implements ILiquiDiskRMi {

    @Autowired
    private RevokeService revokeService;

    @Autowired
    private LiquiRiskSettings liquiConf;

    @Autowired
    private DiskTableService diskTableService;

    @Autowired
    private RevokeScanService revokeScanService;
    
    // ### 盘口挂单
    @RequestMapping(value = "/orders", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> orders() { 
        Map<Object, Object> result = revokeService.getPendingOrders();
        Map<String, JSONObject> orders = new HashMap<>();
        for (Entry<Object, Object> entry : result.entrySet()) {
            orders.put(entry.getKey().toString(), new JSONObject(entry.getValue().toString()));
        }
        return R.ok(orders).message(orders.size() + "");
    }

    // ### 盘口挂单id
    @RequestMapping(value = "/orders-ids", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> orderIds() {
        return R.ok(revokeService.getPendingOrdersIds());
    }
    
    // ### 清除盘口挂单锁
    @RequestMapping(value = "/remove-orders-lock-key", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> removeOrderLockKey() {
        return R.ok(revokeService.removeOrderLockKey());
    }
    
    // ### 查看撤单计数排名
    @RequestMapping(value = "/view-revoke-count-score", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Map<Object, Double>> viewRevokeCountScore() {
        return R.ok(revokeService.viewRevokeCountScore());
    }
    
    // ### 手动撤单
    @RequestMapping(value = "/revoke-manully", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> revokeManully() {
        revokeService.revokeRetryer();
        return R.ok(true);
    }
    
    // ### 手动撤单
    @RequestMapping(value = "/scan-manully", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> scanManully() {
        revokeScanService.scan();
        return R.ok(true);
    }

    // 
    @RequestMapping(value = "/view-strategy-settings", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<LiquiRiskStrategySettings> viewStrategySettings () {
        LiquiRiskStrategySettings settings = liquiConf.getCachedStrategySettings("btcusdt");
        return R.ok(settings);
    }

    // 
    @RequestMapping(value = "/view-alarm-settings", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<LiquiRiskAlarmSettings> viewAlarmSettings () {
        LiquiRiskAlarmSettings settings = liquiConf.getCachedAlarmSettings("btcusdt");
        return R.ok(settings);
    }

    // 
    @RequestMapping(value = "/view-skip-code", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Integer> viewSkipCode () {
        LiquiRiskStrategySettings settings = liquiConf.getCachedStrategySettings("btcusdt");
        OrderBookSkipCodeEnum skipCode = diskTableService.getSkipCode("btcusdt", settings); 
        return R.ok(skipCode.code);
    }
    
}
