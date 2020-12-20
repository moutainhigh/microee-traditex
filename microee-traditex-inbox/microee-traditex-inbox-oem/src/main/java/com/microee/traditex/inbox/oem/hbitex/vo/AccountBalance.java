package com.microee.traditex.inbox.oem.hbitex.vo;


import java.io.Serializable;
import java.util.List;

public class AccountBalance implements Serializable {
    
    private static final long serialVersionUID = -3275604320817026570L;
    
    private Integer id;
    private String type;
    private String state;
    private List<AccountBalanceList> list;
    
    public AccountBalance() {
        
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<AccountBalanceList> getList() {
        return list;
    }

    public void setList(List<AccountBalanceList> list) {
        this.list = list;
    }
    
}