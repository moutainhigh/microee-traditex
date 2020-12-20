package com.microee.traditex.archive.app.mappers;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.microee.traditex.archive.oem.models.DiskOrderTable;

public interface DiskOrderTableMapper {
    int insert(DiskOrderTable record);

    int insertSelective(DiskOrderTable record);

    DiskOrderTable selectByPrimaryKey(String diskClientOrderId);

    int updateByPrimaryKeySelective(DiskOrderTable record);

    int updateByPrimaryKey(DiskOrderTable record);

    // 数据归档
    List<DiskOrderTable> archive(@Param("size") Integer size);

    void delete(@Param("lastId") String lastId);
}