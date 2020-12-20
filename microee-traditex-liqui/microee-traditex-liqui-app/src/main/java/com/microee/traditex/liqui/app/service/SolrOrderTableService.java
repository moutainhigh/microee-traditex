package com.microee.traditex.liqui.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.microee.traditex.liqui.app.mappers.SolrOrderTableMapper;
import com.microee.traditex.liqui.oem.models.SolrOrderTable;

@Service
public class SolrOrderTableService {

    @Autowired
    private SolrOrderTableMapper solrOrderTableMapper;
    
    public boolean save(SolrOrderTable record) {
        solrOrderTableMapper.insertSelective(record);
        return true;
    }
    
}
