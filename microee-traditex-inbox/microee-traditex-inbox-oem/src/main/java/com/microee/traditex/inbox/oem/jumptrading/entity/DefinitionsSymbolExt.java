package com.microee.traditex.inbox.oem.jumptrading.entity;

import java.io.Serializable;

public class DefinitionsSymbolExt implements Serializable {

    private static final long serialVersionUID = -3076589805262524524L;

    private String type;
    private String base;
    private String quote;
    
    public DefinitionsSymbolExt() {
        
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

}