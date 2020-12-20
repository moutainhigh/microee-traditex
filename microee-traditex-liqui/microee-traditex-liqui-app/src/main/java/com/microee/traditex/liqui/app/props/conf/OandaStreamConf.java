package com.microee.traditex.liqui.app.props.conf;


public class OandaStreamConf {

    private String restHost;
    private String streamHost;
    private String accountId;
    private String accessToken;
    private String instrument;

    public OandaStreamConf() {

    }

    public String getRestHost() {
        return restHost;
    }

    public void setRestHost(String restHost) {
        this.restHost = restHost;
    }

    public String getStreamHost() {
        return streamHost;
    }

    public void setStreamHost(String streamHost) {
        this.streamHost = streamHost;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

}