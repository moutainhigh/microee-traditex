package com.microee.traditex.liqui.app.mappers;

import org.apache.ibatis.annotations.Param;
import com.microee.traditex.liqui.oem.models.LiquiRiskAlarmSettings;

public interface LiquiRiskAlarmSettingsMapper {
    int insert(LiquiRiskAlarmSettings record);

    int insertSelective(LiquiRiskAlarmSettings record);

    LiquiRiskAlarmSettings selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LiquiRiskAlarmSettings record);

    int updateByPrimaryKey(LiquiRiskAlarmSettings record);

    LiquiRiskAlarmSettings selectByIdAndSymbol(@Param("id") Long id,
            @Param("strategyId") Long strategyId, @Param("liquiditySymbol") String liquiditySymbol);
}
