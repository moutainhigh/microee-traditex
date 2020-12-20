package com.microee.traditex.liqui.app.mappers;

import com.microee.traditex.liqui.oem.models.RevokeOrderRecord;

public interface RevokeOrderRecordMapper {
    int insert(RevokeOrderRecord record);

    int insertSelective(RevokeOrderRecord record);

    RevokeOrderRecord selectByPrimaryKey(String diskOrderRevokeId);

    int updateByPrimaryKeySelective(RevokeOrderRecord record);

    int updateByPrimaryKey(RevokeOrderRecord record);
}