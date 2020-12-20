package com.microee.traditex.liqui.app.actions;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.microee.plugin.response.R;
import com.microee.traditex.liqui.app.components.LiquiRiskSettings;
import com.microee.traditex.liqui.app.mappers.LiquiRiskAlarmSettingsMapper;
import com.microee.traditex.liqui.app.mappers.LiquiRiskStrategySettingsMapper;
import com.microee.traditex.liqui.oem.models.LiquiRiskAlarmSettings;
import com.microee.traditex.liqui.oem.models.LiquiRiskStrategySettings;
import com.microee.traditex.liqui.oem.rmi.LiquiRiskRestfulRMi;

// 风控
@RestController
@RequestMapping("/liqui/risk")
public class LiquiRiskRestful implements LiquiRiskRestfulRMi {

    @Autowired
    private LiquiRiskStrategySettingsMapper riskStrategySettingsMapper;

    @Autowired
    private LiquiRiskAlarmSettingsMapper riskAlarmSettingsMapper;

    @Autowired
    private LiquiRiskSettings liquiConf;
    
    @Override
    @RequestMapping(value = "/strategy/list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<LiquiRiskStrategySettings>> strategyList(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "liquiditySymbol", required = false) String liquiditySymbol) {
        return R.ok(riskStrategySettingsMapper.selectByIdAndSymbol(id == null ? null : id, liquiditySymbol == null || liquiditySymbol.trim().isEmpty() ? null : liquiditySymbol));
    }

    @Override
    @RequestMapping(value = "/strategy/save", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Long> create(@RequestBody LiquiRiskStrategySettings body) {
        Assertions.assertThat(body.getDiskBase()).withFailMessage("%s 必传", "diskBase").isNotBlank();
        Assertions.assertThat(body.getDiskQuote()).withFailMessage("%s 必传", "diskQuote").isNotBlank();
        riskStrategySettingsMapper.insertSelective(body);
        liquiConf.cachedStrategySettings(body);
        return R.ok(body.getId());
    }

    @Override
    @RequestMapping(value = "/alarm/settings/get", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<LiquiRiskAlarmSettings> getAlarmSettings(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "strategyId", required = false) Long strategyId,
            @RequestParam(value = "liquiditySymbol", required = false) String liquiditySymbol) {
        return R.ok(riskAlarmSettingsMapper.selectByIdAndSymbol(id, strategyId, liquiditySymbol == null || liquiditySymbol.trim().isEmpty() ? null : liquiditySymbol));
    }

    @Override
    @RequestMapping(value = "/strategy/update", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> updateStrategy(@RequestBody LiquiRiskStrategySettings body) {
        if (body.getId() == null) {
            riskStrategySettingsMapper.updateExchangeConfig(body);
            List<LiquiRiskStrategySettings> list = riskStrategySettingsMapper.selectByIdAndSymbol(null, null);
            for (int i=0; i<list.size(); i++) {
                liquiConf.cachedStrategySettings(list.get(i));
            }
            return R.ok(true);
        }
        riskStrategySettingsMapper.updateByPrimaryKeySelective(body);
        LiquiRiskStrategySettings settings = riskStrategySettingsMapper.selectByPrimaryKey(body.getId());
        liquiConf.cachedStrategySettings(settings);
        return R.ok(true);
    }

    @Override
    @RequestMapping(value = "/alarm/update", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> modifyAlarmSettings(@RequestBody LiquiRiskAlarmSettings body) {
        boolean b = riskAlarmSettingsMapper.updateByPrimaryKeySelective(body) > 0;
        if (b) {
            LiquiRiskAlarmSettings settings = riskAlarmSettingsMapper.selectByPrimaryKey(body.getId());
            liquiConf.cachedAlarmSettings(settings);
        }
        return R.ok(b);
    }

    @Override
    @RequestMapping(value = "/alarm/add", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> addAlarmSettings(@RequestBody LiquiRiskAlarmSettings body) {
        int i = riskAlarmSettingsMapper.insertSelective(body);
        LiquiRiskAlarmSettings settings = riskAlarmSettingsMapper.selectByPrimaryKey(body.getId());
        liquiConf.cachedAlarmSettings(settings);
        return R.ok(i > 0);
    }
}
