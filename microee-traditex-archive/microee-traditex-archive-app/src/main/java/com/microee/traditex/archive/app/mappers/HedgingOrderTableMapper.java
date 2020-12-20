package com.microee.traditex.archive.app.mappers;

import com.microee.traditex.archive.oem.models.HedgingOrderTable;

public interface HedgingOrderTableMapper {
    int insert(HedgingOrderTable record);

    int insertSelective(HedgingOrderTable record);

    HedgingOrderTable selectByPrimaryKey(String orderId);

    int updateByPrimaryKeySelective(HedgingOrderTable record);

    int updateByPrimaryKey(HedgingOrderTable record);
}