package com.microee.traditex.liqui.app.mappers;

import com.microee.traditex.liqui.oem.models.SolrOrderTable;

public interface SolrOrderTableMapper {
    int insert(SolrOrderTable record);

    int insertSelective(SolrOrderTable record);

    SolrOrderTable selectByPrimaryKey(String solrClientOrderId);

    int updateByPrimaryKeySelective(SolrOrderTable record);

    int updateByPrimaryKey(SolrOrderTable record);
}