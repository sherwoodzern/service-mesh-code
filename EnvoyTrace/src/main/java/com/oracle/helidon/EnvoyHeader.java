package com.oracle.helidon;

import java.util.Hashtable;
import java.util.List;

public class EnvoyHeader {

    public static final String X_CLIENT_TRACE_ID    = "x-client-trace-id";
    public static final String X_REQUEST_ID         = "x-request-id";
    public static final String X_B3_TRACEID         = "x-b3-traceid";
    public static final String X_B3_SPANID          = "x-b3-spanid";
    public static final String X_B3_PARENTSPANID    = "x-b3-parentspanid";
    public static final String X_B3_SAMPLED         = "x-b3-sampled";
    public static final String X_B3_FLAGS           = "x-b3-flags";
    public static final String X_B3_OT_SPAN_CONTEXT = "x-ot-span-context";

    private Hashtable<String, List<String>> header = new Hashtable<String,List<String>>(); 
    

    public EnvoyHeader () {

    }

    public Hashtable<String, List<String>> getHeader() {
        return header;
    }

    public void addHeader(String key, List<String> value) {
        header.put(key,value);
    }
            
}