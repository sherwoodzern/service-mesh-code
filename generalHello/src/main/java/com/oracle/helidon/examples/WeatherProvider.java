package com.oracle.helidon.examples;

import java.net.URL;

import javax.json.Json;
import javax.json.JsonReader;

//import javax.json.JsonValue;
import javax.json.JsonObject;
import javax.json.JsonException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.ws.rs.core.HttpHeaders;
import com.oracle.helidon.EnvoyHeader;
import com.oracle.helidon.EnvoyTrace;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Collection;


/* 
 * This is a helper class that knows how to build the invocation
 * to the weather service.
 * */

 public class WeatherProvider {

    private String units;
    private String zipcode;
    private HttpHeaders httpHeaders;

    private static final Logger logger = Logger.getLogger("com.oracle.helidon.examples");

    public WeatherProvider () {
        // Defaults to Washington, D.C.

    }

    public WeatherProvider(String units, String zipcode, HttpHeaders httpHeaders) {
        this.units = units;
        this.zipcode = zipcode;
        this.httpHeaders = httpHeaders;
        
    }

    public JsonObject getWeatherByZip() {
        
        logger.log(Level.INFO, "Entered getWeatherByZip()");
        HttpURLConnection connection = null;
        JsonReader jsonReader = null;
        JsonObject jsonObject = null;
        try {
            connection = getHttpConnection("GET");
            EnvoyHeader header = getAllHeaders();
            EnvoyTrace tracer = new EnvoyTrace(header);
            tracer.addTraceHeaderToRequest(connection);
            logger.log(Level.INFO, connection.getURL().toString());
            jsonReader = Json.createReader(connection.getInputStream());
            jsonObject = jsonReader.readObject();
            // In order to obtain the temperature in Fahrenheit specify
            // units=imperial
            
        }
        catch (MalformedURLException mfe) {
            jsonObject = createException(mfe);
        }
        catch (IOException ioe) {
            jsonObject = createException(ioe);
        }
        finally {
            if (null != connection)
                connection.disconnect();
            try {
                if (null != jsonReader)
                    jsonReader.close();
            }
            catch (JsonException je) {}
        }

        return jsonObject;
        
    }


    private HttpURLConnection getHttpConnection(String method) 
        throws MalformedURLException, IOException {

        URL url = getURLConnection();
        logger.log(Level.INFO, "Returned from the getURLConnection");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        logger.log(Level.INFO, "HttpURLConnection has been created");
        conn.setRequestMethod(method);
        conn.setRequestProperty("Accept", "application/json");
        System.out.println("Connection is created");
        return conn;
         
    }
    
    private URL getURLConnection() throws MalformedURLException {
        
        String path = buildPath();
        // Needs to be http://localhost:3100/forecast
        URL url = new URL(System.getenv("WEATHER_PROXY_URL") + path);
        logger.log(Level.INFO, url.getHost() + url.getPath());
        return url;
        
    }

    // The weather URL will be http://<host>:3100/forecast
    // Need to add units=<some value>. The possible values can be
    // kelvin, metrics, imperial
    private String buildPath() {
        StringBuilder bldr = new StringBuilder();
        bldr.append("/zip/");
        bldr.append(zipcode);
        bldr.append("/units/");
        bldr.append(units);
        return bldr.toString();
    }

    private JsonObject createException(Exception e) {
        JsonObject jsonObject = Json.createObjectBuilder()
            .add("Exception:", e.getMessage())
            .add("Status:", "Failed")
            .build();
            return jsonObject;
    }

    private EnvoyHeader getAllHeaders() {
        MultivaluedMap <String, String> headerValues = httpHeaders.getRequestHeaders();
        Collection<List<String>> values = headerValues.values();
        Object[] elements = values.toArray();
        for (int i = 0; i < elements.length; i++ ){
            logger.log(Level.INFO, (String) elements[i]);
        }
        return new EnvoyHeader();
    }



 }