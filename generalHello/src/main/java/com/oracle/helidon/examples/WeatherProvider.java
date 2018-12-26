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

/* 
 * This is a helper class that knows how to build the invocation
 * to the weather service.
 * */

 public class WeatherProvider {

    private String units;
    private String zipcode;

    public WeatherProvider () {
        // Defaults to Washington, D.C.
        this("20001", "imperial");

    }

    public WeatherProvider(String units, String zipcode) {
        this.units = units;
        this.zipcode = zipcode;
    }

    public JsonObject getWeatherByZip() {
        
        HttpURLConnection connection = null;
        JsonReader jsonReader = null;
        try {
            connection = getHttpConnection("GET");
            jsonReader = Json.createReader(connection.getInputStream());
            JsonObject jsonObject = jsonReader.readObject();
            // In order to obtain the temperature in Fahrenheit specify
            // units=imperial
            return jsonObject;
        }
        catch (MalformedURLException mfe) {
            return Json.createObjectBuilder()
                .add("exception", mfe.getMessage())
                .add("status", "Failed")
                .build();
        }
        catch (IOException ioe) {
            return Json.createObjectBuilder()
                .add("exception", ioe.getMessage())
                .add("status", "Failed")
                .build();
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
        
    }


    private HttpURLConnection getHttpConnection(String method) 
        throws MalformedURLException, IOException {

        URL url = getURLConnection();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Accept", "application/json");
        System.out.println("Connection is created");
        return conn;
         
    }
    
    private URL getURLConnection() throws MalformedURLException {
        
        String path = buildPath();
        // Needs to be http://localhost:32000/forecast
        URL url = new URL(System.getenv("WEATHER_SERVICE_URL") + path);
        return url;
        
    }

    // The weather URL will be http://localhost:32000/forecast
    // Need to add units=<some value>. The possible values can be
    // kelvin, metrics, imperial
    private String buildPath() {
        StringBuilder bldr = new StringBuilder();
        bldr.append("/");
        bldr.append("zip");
        bldr.append("/");
        bldr.append(zipcode);
        bldr.append("/");
        bldr.append("units");
        bldr.append("/");
        bldr.append(units);
        return bldr.toString();
    }



 }