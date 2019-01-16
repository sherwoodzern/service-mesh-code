package com.oracle.helidon;

import java.net.HttpURLConnection;
import java.util.function.BiConsumer;
import java.util.List;
//import javax.ws.rs.core.HttpHeaders;
/**
 * 
 *
 */
public class EnvoyTrace 
{
    private EnvoyHeader envoyHeader;

    public EnvoyTrace (EnvoyHeader envoyHeader) {
        this.envoyHeader = envoyHeader;
    }

    public void addTraceHeaderToRequest(HttpURLConnection connection) {
        BiConsumer <String, List<String>> process = (key, value) ->
            connection.setRequestProperty(key, value.get(0));
        
        envoyHeader.getHeader().forEach(process);
    }

}
