package com.microee.traditex.archive.app.mappers;

import com.microee.traditex.archive.oem.models.DiskOrderDetail;

public interface DiskOrderDetailMapper {
    int insert(DiskOrderDetail record);

    int insertSelective(DiskOrderDetail record);

    DiskOrderDetail selectByPrimaryKey(String diskDetailId);

    int updateByPrimaryKeySelective(DiskOrderDetail record);

    int updateByPrimaryKey(DiskOrderDetail record);
}