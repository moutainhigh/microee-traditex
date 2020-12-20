package com.microee.traditex.liqui.app.mappers;

import com.microee.traditex.liqui.oem.models.SolrNextOrderTable;

public interface SolrNextOrderTableMapper {
    int insert(SolrNextOrderTable record);

    int insertSelective(SolrNextOrderTable record);

    SolrNextOrderTable selectByPrimaryKey(String nextSolrId);

    int updateByPrimaryKeySelective(SolrNextOrderTable record);

    int updateByPrimaryKey(SolrNextOrderTable record);

    SolrNextOrderTable selectNextSolrOrder();
}
