package com.microee.traditex.archive.app.mappers;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.microee.traditex.archive.oem.models.OrderbookStream;

public interface OrderbookStreamMapper {
    int insert(OrderbookStream record);

    int insertSelective(OrderbookStream record);

    OrderbookStream selectByPrimaryKey(String orderBookId);

    int updateByPrimaryKeySelective(OrderbookStream record);

    int updateByPrimaryKey(OrderbookStream record);

    // 数据归档
    List<OrderbookStream> archive(@Param("size") Integer size);

    void delete(@Param("lastId") String lastId);
    
}