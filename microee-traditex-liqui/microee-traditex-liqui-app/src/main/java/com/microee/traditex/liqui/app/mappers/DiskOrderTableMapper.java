package com.microee.traditex.liqui.app.mappers;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.microee.traditex.liqui.oem.models.DiskOrderTable;

public interface DiskOrderTableMapper {
    int insert(DiskOrderTable record);

    int insertSelective(DiskOrderTable record);

    DiskOrderTable selectByPrimaryKey(String diskClientOrderId);

    int updateByPrimaryKeySelective(DiskOrderTable record);

    int updateByPrimaryKey(DiskOrderTable record);
    
    // 批量保存
    int insertList(@Param("list") List<DiskOrderTable> record);
    
}