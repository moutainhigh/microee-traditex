package com.microee.traditex.archive.app.mappers;

import com.microee.traditex.archive.oem.models.SolrOrderTable;

public interface SolrOrderTableMapper {
    int insert(SolrOrderTable record);

    int insertSelective(SolrOrderTable record);

    SolrOrderTable selectByPrimaryKey(String solrClientOrderId);

    int updateByPrimaryKeySelective(SolrOrderTable record);

    int updateByPrimaryKey(SolrOrderTable record);
}