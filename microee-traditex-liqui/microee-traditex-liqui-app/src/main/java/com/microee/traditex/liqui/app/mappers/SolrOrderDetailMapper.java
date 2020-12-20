package com.microee.traditex.liqui.app.mappers;

import com.microee.traditex.liqui.oem.models.SolrOrderDetail;

public interface SolrOrderDetailMapper {
    int insert(SolrOrderDetail record);

    int insertSelective(SolrOrderDetail record);

    SolrOrderDetail selectByPrimaryKey(String solrClientOrderId);

    int updateByPrimaryKeySelective(SolrOrderDetail record);

    int updateByPrimaryKey(SolrOrderDetail record);
}