package com.microee.traditex.liqui.app.mappers;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.microee.traditex.liqui.oem.models.DiskOrderDetail;

public interface DiskOrderDetailMapper {
    int insert(DiskOrderDetail record);

    int insertSelective(DiskOrderDetail record);

    DiskOrderDetail selectByPrimaryKey(String diskClientOrderId);

    int updateByPrimaryKeySelective(DiskOrderDetail record);

    int updateByPrimaryKey(DiskOrderDetail record);
    
    List<DiskOrderDetail> selectByDiskOrderId(@Param("diskOrderId") String diskOrderId);
    
}