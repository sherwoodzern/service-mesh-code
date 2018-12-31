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

import javax.enterprise.context.RequestScoped;
//import java.net.URL;
//import java.net.HttpURLConnection;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
//import javax.json.JsonValue;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * A simple JAX-RS resource to greet you. Examples:
 *
 * Get default greeting message:
 * curl -X GET http://localhost:8080/greet
 *
 * Get greeting message for Joe:
 * curl -X GET http://localhost:8080/greet/Joe
 *
 * Change greeting
 * curl -X PUT http://localhost:8080/greet/greeting/Hola
 *
 * The message is returned as a JSON object.
 */
@Path("/greet")
@RequestScoped
public class GreetResource {

    /**
     * The greeting message provider.
     */
    private final GreetingProvider greetingProvider;
    private Logger logger = Logger.getLogger("com.oracle.helidon.examples");

    /**
     * Using constructor injection to get a configuration property.
     * By default this gets the value from META-INF/microprofile-config
     *
     * @param greetingConfig the configured greeting message
     */
    @Inject
    public GreetResource(GreetingProvider greetingConfig) {
        this.greetingProvider = greetingConfig;
    }

    /**
     * Return a wordly greeting message.
     *
     * @return {@link JsonObject}
     */
    @SuppressWarnings("checkstyle:designforextension")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getDefaultMessage() {
        logger.log(Level.INFO,"Invoking the getDefaultMessage");
        JsonObject js = createResponse("World");
        logger.log(Level.INFO, "Dumping the JsonObject", js);
        return js;
    }

    /**
     * Return a greeting message using the name that was provided.
     *
     * @param name the name to greet
     * @return {@link JsonObject}
     */
    @SuppressWarnings("checkstyle:designforextension")
    @Path("/{name}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getMessage(@PathParam("name") String name) {
        return createResponse(name);
    }

    /**
     * Set the greeting to use in future messages.
     *
     * @param newGreeting the new greeting message
     * @return {@link JsonObject}
     */
    @SuppressWarnings("checkstyle:designforextension")
    @Path("/greeting/{greeting}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject updateGreeting(@PathParam("greeting") String newGreeting) {
        greetingProvider.setMessage(newGreeting);

        return Json.createObjectBuilder()
                .add("greeting", newGreeting)
                .build();
    }

   /**
     * Return the weather to the individual specified by "name"
     * and for the specified "zipcode".
     * 
     * @param name of the requester
     * @param zipcode the weather for the specified location, by zipcode
     * 
     * @return {@link JsonObject}
     */
    @SuppressWarnings("checkstyle:designforextension")
    @Path("/{name}/weather/{zipcode}/units/{units}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getWeatherByZipcode(
        @PathParam("name") String name, 
        @PathParam("zipcode") String zipcode, 
        @PathParam("units") String units) {
        
        WeatherProvider weather = new WeatherProvider(units,zipcode);
        JsonObject response = weather.getWeatherByZip();

        // Once we have the response then we need 
        // to format the response appropriately

        JsonObjectBuilder target = Json.createObjectBuilder();
        response.forEach(target::add); // copy source into target
        target.add("Requestor", name); // add or update values
        JsonObject destination = target.build(); // build destination


        return destination;
        
        
        // build URL for weather invocation
        // make call to weather service
        // Get response from the weather service
        // Retrieve the current temperature for the zip code
        // Format the message
        // return the response object

        // JsonObject response = url.getResponse();
        // String temperature = response.getTemperature();

        /*return Json.createObjectBuilder()
                .add("greeting", newGreeting)
                .build();*/
    }

    /**
     * Return the weather to the individual specified by "name"
     * and for the specified "zipcode".
     * 
     * @param name of the requester
     * @param zipcode the weather for the specified location, by zipcode
     * 
     * @return {@link JsonObject}
     */
    @SuppressWarnings("checkstyle:designforextension")
    @Path("/fn/{name}/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject helloFromFn(
        @PathParam("name") String newGreeting) {
    
        
        greetingProvider.setMessage(newGreeting);
        // build URL for weather invocation
        // make call to weather service
        // Get response from the weather service
        // Retrieve the current temperature for the zip code
        // Format the message
        // return the response object

        // JsonObject response = url.getResponse();
        // String temperature = response.getTemperature();

        return Json.createObjectBuilder()
                .add("greeting", newGreeting)
                .build();
    }



    private JsonObject createResponse(String who) {
        String msg = String.format("%s %s!", greetingProvider.getMessage(), who);

        return Json.createObjectBuilder()
                .add("message", msg)
                .build();
    }
}
