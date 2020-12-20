package com.microee.traditex.archive.app.mappers;

import com.microee.traditex.archive.oem.models.SolrNextOrderTable;

public interface SolrNextOrderTableMapper {
    int insert(SolrNextOrderTable record);

    int insertSelective(SolrNextOrderTable record);

    SolrNextOrderTable selectByPrimaryKey(String nextSolrId);

    int updateByPrimaryKeySelective(SolrNextOrderTable record);

    int updateByPrimaryKey(SolrNextOrderTable record);
}