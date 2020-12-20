package com.microee.traditex.inbox.oem.jumptrading.apiresult;

import java.io.Serializable;
import java.util.List;

public class JumpTradingApiResultForMarketData extends JumpTradingApiResultBase
        implements Serializable {

    private static final long serialVersionUID = 3490961010889428485L;

    private List<String> subSymbols;
    private Object books;

    public List<String> getSubSymbols() {
        return subSymbols;
    }

    public void setSubSymbols(List<String> subSymbols) {
        this.subSymbols = subSymbols;
    }

    public Object getBooks() {
        return books;
    }

    public void setBooks(Object books) {
        this.books = books;
    }

}
