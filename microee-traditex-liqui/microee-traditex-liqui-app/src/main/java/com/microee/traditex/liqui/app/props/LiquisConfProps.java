package com.microee.traditex.liqui.app.props;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import com.microee.traditex.liqui.app.props.conf.Conf;

@Configuration
@ConfigurationProperties(prefix = "liquis")
public class LiquisConfProps {

    private Boolean revokeEnable = false;
    private Boolean debugOneOrder = false; // 只挂一个订单

    private List<Confs> confs;

    public LiquisConfProps() {

    }

    public Boolean getDebugOneOrder() {
        return debugOneOrder;
    }

    public void setDebugOneOrder(Boolean debugOneOrder) {
        this.debugOneOrder = debugOneOrder;
    }

    public Boolean getRevokeEnable() {
        return revokeEnable;
    }

    public void setRevokeEnable(Boolean revokeEnable) {
        this.revokeEnable = revokeEnable;
    }

    public List<Confs> getConfs() {
        return confs;
    }

    public void setConfs(List<Confs> confs) {
        this.confs = confs;
    }

    public static class Confs {

        private Conf orderbook;
        private Conf disk;
        private Conf solr;
        private Conf hedging;

        public Confs() {


        }

        public Conf getOrderbook() {
            return orderbook;
        }

        public void setOrderbook(Conf orderbook) {
            this.orderbook = orderbook;
        }

        public Conf getDisk() {
            return disk;
        }

        public void setDisk(Conf disk) {
            this.disk = disk;
        }

        public Conf getSolr() {
            return solr;
        }

        public void setSolr(Conf solr) {
            this.solr = solr;
        }

        public Conf getHedging() {
            return hedging;
        }

        public void setHedging(Conf hedging) {
            this.hedging = hedging;
        }

    }

}
