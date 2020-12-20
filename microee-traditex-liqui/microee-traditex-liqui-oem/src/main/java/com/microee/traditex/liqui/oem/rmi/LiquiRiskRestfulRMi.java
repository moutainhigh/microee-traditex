package com.microee.traditex.liqui.oem.rmi;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.microee.plugin.response.R;
import com.microee.traditex.liqui.oem.models.LiquiRiskAlarmSettings;
import com.microee.traditex.liqui.oem.models.LiquiRiskStrategySettings;

public interface LiquiRiskRestfulRMi {

    // strategy list
    @RequestMapping(value = "/strategy/list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<LiquiRiskStrategySettings>> strategyList(@RequestParam(value = "id", required=false) Long id, @RequestParam(value = "liquiditySymbol", required=false) String liquiditySymbol);

    // 保存 strategy
    @RequestMapping(value = "/strategy/save", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Long> create(@RequestBody LiquiRiskStrategySettings body);

    @RequestMapping(value = "/alarm/settings/get", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<LiquiRiskAlarmSettings> getAlarmSettings(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "strategyId", required = false) Long strategyId,
            @RequestParam(value = "liquiditySymbol", required = false) String liquiditySymbol);

    @RequestMapping(value = "/strategy/update", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> updateStrategy(LiquiRiskStrategySettings settingsDO);

    @RequestMapping(value = "/alarm/update", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> modifyAlarmSettings(LiquiRiskAlarmSettings aDo);

    @RequestMapping(value = "/alarm/add", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> addAlarmSettings(LiquiRiskAlarmSettings aDo);
}
