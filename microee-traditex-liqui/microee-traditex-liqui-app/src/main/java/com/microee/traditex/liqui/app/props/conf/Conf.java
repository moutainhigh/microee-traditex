package com.microee.traditex.liqui.app.props.conf;

import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.http.assets.HttpAssets;

public class Conf {

    private Boolean proxyEnable;
    private Boolean autoconnect = false;
    private String vender;
    private Map<String, Object> maps;

    public Conf() {

    }

    public Boolean getProxyEnable() {
        return proxyEnable;
    }

    public void setProxyEnable(Boolean proxyEnable) {
        this.proxyEnable = proxyEnable;
    }

    public Boolean getAutoconnect() {
        return autoconnect;
    }

    public void setAutoconnect(Boolean autoconnect) {
        this.autoconnect = autoconnect;
    }

    public String getVender() {
        return vender;
    }

    public void setVender(String vender) {
        this.vender = vender;
    }

    public Map<String, Object> getMaps() {
        return maps;
    }

    public void setMaps(Map<String, Object> maps) {
        this.maps = maps;
    }

    public <T> T map(TypeReference<T> typeRef) {
        return HttpAssets.parseMap(this.maps, typeRef);
    }

}
