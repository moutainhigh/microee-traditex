package com.microee.traditex.archive.app.mappers;

import com.microee.traditex.archive.oem.models.SolrOrderDetail;

public interface SolrOrderDetailMapper {
    int insert(SolrOrderDetail record);

    int insertSelective(SolrOrderDetail record);

    SolrOrderDetail selectByPrimaryKey(String solrDetailId);

    int updateByPrimaryKeySelective(SolrOrderDetail record);

    int updateByPrimaryKey(SolrOrderDetail record);
}