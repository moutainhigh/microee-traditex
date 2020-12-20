package com.microee.traditex.inbox.oem.b2c2.subscribe;

import java.io.Serializable;
import java.util.UUID;

public class BCSubscribeInstrument implements Serializable {

    private static final long serialVersionUID = -3678302970813360504L;

    private String event;
    private String instrument;
    private Float[] levels;
    private String tag;

    public BCSubscribeInstrument() {

    }

    public static BCSubscribeInstrument create(String symbol, boolean subscribe, Float[] levels) {
        return new BCSubscribeInstrument(symbol + ".SPOT", subscribe, levels);
    }

    public BCSubscribeInstrument(String instrument, boolean subscribe, Float[] levels) {
        this.event = subscribe ? "subscribe" : "unsubscribe";
        this.instrument = instrument;
        this.tag = UUID.randomUUID().toString().split("-")[0];
        this.levels = levels;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public Float[] getLevels() {
        return levels;
    }

    public void setLevels(Float[] levels) {
        this.levels = levels;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public static Float[] getLevelsByCounter(String symbol, String counter) {
        if (counter == null) {
            return null;
        }
        if (counter.equals("BTC")) {
            return new Float[] {0.5f};
        }
        if (counter.equals("BCH")) {
            return new Float[] {50f};
        }
        if (counter.equals("ETH")) {
            return new Float[] {50f};
        }
        if (counter.equals("XRP")) {
            return new Float[] {50000f};
        }
        if (counter.equals("LTC")) {
            return new Float[] {50000f};
        }
        if (symbol.equalsIgnoreCase("BTCJPY")) {
            return new Float[] {0.001f, 100f};
        }
        if (symbol.equalsIgnoreCase("ETHJPY")) {
            return new Float[] {0.01f, 1000f};
        }
        if (symbol.equalsIgnoreCase("LTCJPY")) {
            return new Float[] {0.1f, 4000f};
        }
        if (symbol.equalsIgnoreCase("BTCUST")) {
            return new Float[] {0.001f, 100f};
        }
        if (symbol.equalsIgnoreCase("BCHJPY")) {
            return new Float[] {0.01f, 500f};
        }
        if (symbol.equalsIgnoreCase("XRPJPY")) {
            return new Float[] {100.0f, 200000f};
        }
        return new Float[] {100.0f, 200000f};
    }

}
