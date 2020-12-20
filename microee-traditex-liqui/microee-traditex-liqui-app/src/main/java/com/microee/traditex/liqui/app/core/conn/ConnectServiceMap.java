package com.microee.traditex.liqui.app.core.conn;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.traditex.liqui.app.props.conf.HBiTexAccountConf;

@Component
public class ConnectServiceMap {

    public Map<String, String> connCacheMapTypeAndId = new HashMap<>();
    public Map<String, Map<String, Object>> connCacheMapConnIdAndAccount = new HashMap<>();
    public Map<String, String> connAuthCacheMapIdAndType = new HashMap<>();
    public Map<String, HBiTexAccountConf> cachedHBiTexAccountMap = new HashMap<>();
    
    @PostConstruct
    public void init() {
        
    }
    
    public void putTypeAndId(String type, String connid) {
        this.connCacheMapTypeAndId.put(type, connid);
    }
    
    public String getConnidByType(String type) {
        if (this.connCacheMapTypeAndId.containsKey(type)) {
            return this.connCacheMapTypeAndId.get(type);
        }
        return null;
    }
    
    public void putConnIdAndAccount(String connid, Object account) {
        Map<String, Object> accountMap = HttpAssets.parseJson(HttpAssets.toJsonString(account), new TypeReference<Map<String, Object>>() {});
        this.connCacheMapConnIdAndAccount.put(connid, accountMap);
    }
    
    public void putAuthIdAndVender(String connid, String vender) {
        this.connAuthCacheMapIdAndType.put(connid, vender);
    }
    
    public String gettAuthIdAndVender(String connid) {
        if (this.connAuthCacheMapIdAndType.containsKey(connid)) {
            return this.connAuthCacheMapIdAndType.get(connid);
        }
        return null;
    }
    
    public void putHBiTexAccount (String uid, HBiTexAccountConf account) {
        this.cachedHBiTexAccountMap.put(uid, account);
    }

    public HBiTexAccountConf getAccountByUid(String uid) {
        if (this.cachedHBiTexAccountMap.containsKey(uid)) {
            return this.cachedHBiTexAccountMap.get(uid);
        }
        return null;
    }
    
    public <T> T getAccountByConnId(String connid, TypeReference<T> typeRef) {
        if (this.connCacheMapConnIdAndAccount.containsKey(connid)) {
            Map<String, Object> map = this.connCacheMapConnIdAndAccount.get(connid);
            return HttpAssets.parseMap(map, typeRef);
        }
        return null;
    }
    
    public String[] connids() {
        return connCacheMapTypeAndId.values().toArray(new String[this.connCacheMapTypeAndId.size()]);
    }
    
    public Map<String, String> connidMaps() {
        return this.connCacheMapTypeAndId;
    }
    
}
