package com.microee.traditex.archive.app.mappers;

import com.microee.traditex.archive.oem.models.LiquiArchiveRecord;

public interface LiquiArchiveRecordMapper {
    int insert(LiquiArchiveRecord record);

    int insertSelective(LiquiArchiveRecord record);

    LiquiArchiveRecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(LiquiArchiveRecord record);

    int updateByPrimaryKey(LiquiArchiveRecord record);
}