package com.microee.traditex.inbox.up;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TradiTexAssists {

    public static String shorterContent(String str, short len) {
        if (str == null) {
            return null;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len) + " ... ";
    }
    
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    
}
