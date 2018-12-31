/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oracle.helidon.examples;

import java.util.concurrent.atomic.AtomicReference;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.Json;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Logger;
//import java.util.logging.Level;


/**
 * Provider for greeting message.
 */
@ApplicationScoped
public class WeatherService {
    private final AtomicReference<String> zipcode = new AtomicReference<>();
    private final AtomicReference<String> units   = new AtomicReference<>();
    private Logger logger = Logger.getLogger("com.oracle.helidon.examples");

    /**
     * Create a new greeting provider, reading the message from configuration.
     *
     * @param message greeting to use
     */
    @Inject
    public WeatherService() {
        
    }

	/**
	 * @return the zipcode
	 */
	public String getZipcode() {
		return zipcode.get();
	}

	/**
	 * @return the units
	 */
	public String getUnits() {
		return units.get();
    }
    
    /** 
     * set the zipcode
     */
    public void setZipcode(String zip) {
        this.zipcode.set(zip);
    }

    /** 
     * Set the units to convert the weather values to
     * Valid values are kelvin, imperial, metrics
     */
    public void setUnits(String units) {
        this.units.set(units);
    }

    public JsonObject getWeatherByZip() {
        JsonObject js = getWeather();
        return js;
    }

    private JsonObject getWeather() {
        logger.info("Invoking the getWeather method");
        String query = createQueryString();
        logger.info("The query string: " + query);
        JsonReader jsonReader = null;
        JsonObject jsonObject = null;
        HttpURLConnection connection = null;
        try {
            connection = getURLConnection("GET", query);
            jsonReader = Json.createReader(connection.getInputStream());
            jsonObject = jsonReader.readObject();
        } catch (Exception e) {
            jsonObject = createExceptionResponse(e);
        }
        finally {
            if (jsonReader != null) {
                jsonReader.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return jsonObject;
    }

    private String createQueryString() {
        StringBuilder queryString = new StringBuilder("?");
        queryString.append("zip=");
        queryString.append(getZipcode());
        queryString.append("&units=");
        queryString.append(getUnits());
        queryString.append("&apiKey=");
        queryString.append(System.getenv("apiKey"));
        return queryString.toString();
    }

    private HttpURLConnection getURLConnection(String method, String path) 
        throws Exception {
            URL url = new URL(System.getenv("hostURL") + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            return connection;

    } 

    private JsonObject createExceptionResponse(Exception e) {
        JsonObject response = Json.createObjectBuilder()
            .add("exception", e.getMessage())
            .add("status", "Failed")
            .build();
        return response;
    }



}
