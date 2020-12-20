package com.microee.traditex.liqui.app.mappers;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.microee.traditex.liqui.oem.models.OrderbookStream;

public interface OrderbookStreamMapper {
    int insert(OrderbookStream record);

    int insertSelective(OrderbookStream record);

    OrderbookStream selectByPrimaryKey(String orderBookId);

    int updateByPrimaryKeySelective(OrderbookStream record);

    int updateByPrimaryKey(OrderbookStream record);


    // 批量保存
    int insertList(@Param("list") List<OrderbookStream> record);

    OrderbookStream selectByBookId(@Param("bookId") String bookId);
    
}