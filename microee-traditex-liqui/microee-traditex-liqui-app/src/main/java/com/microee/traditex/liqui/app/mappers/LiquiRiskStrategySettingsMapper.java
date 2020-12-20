package com.microee.traditex.liqui.app.mappers;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.microee.traditex.liqui.oem.models.LiquiRiskStrategySettings;

public interface LiquiRiskStrategySettingsMapper {
    int insert(LiquiRiskStrategySettings record);

    int insertSelective(LiquiRiskStrategySettings record);

    LiquiRiskStrategySettings selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LiquiRiskStrategySettings record);

    int updateByPrimaryKeyWithBLOBs(LiquiRiskStrategySettings record);

    int updateByPrimaryKey(LiquiRiskStrategySettings record);

    List<LiquiRiskStrategySettings> selectByIdAndSymbol(@Param("id") Long id,
            @Param("liquiditySymbol") String liquiditySymbol);

    void updateExchangeConfig(LiquiRiskStrategySettings record);
}
