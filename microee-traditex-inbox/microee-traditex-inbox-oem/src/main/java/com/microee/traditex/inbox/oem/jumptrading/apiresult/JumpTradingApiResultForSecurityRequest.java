package com.microee.traditex.inbox.oem.jumptrading.apiresult;

import java.io.Serializable;
import java.util.Map;
import com.microee.traditex.inbox.oem.jumptrading.entity.DefinitionsSymbol;

public class JumpTradingApiResultForSecurityRequest extends JumpTradingApiResultBase
        implements Serializable {

    private static final long serialVersionUID = -9166764068836481519L;

    private Map<String, DefinitionsSymbol> definitions;
    
    public JumpTradingApiResultForSecurityRequest() {
        
    }

    public Map<String, DefinitionsSymbol> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<String, DefinitionsSymbol> definitions) {
        this.definitions = definitions;
    }

}
