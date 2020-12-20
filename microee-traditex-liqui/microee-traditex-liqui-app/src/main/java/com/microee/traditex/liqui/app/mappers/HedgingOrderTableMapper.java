package com.microee.traditex.liqui.app.mappers;

import com.microee.traditex.liqui.oem.models.HedgingOrderTable;

public interface HedgingOrderTableMapper {
    int insert(HedgingOrderTable record);

    int insertSelective(HedgingOrderTable record);

    HedgingOrderTable selectByPrimaryKey(String diskClientOrderId);

    int updateByPrimaryKeySelective(HedgingOrderTable record);

    int updateByPrimaryKey(HedgingOrderTable record);
}