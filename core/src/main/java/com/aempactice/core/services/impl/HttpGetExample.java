package com.aempactice.core.services.impl;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HttpGetExample {
    public static void main(String[] args) {
        try {
            // Create an instance of HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Create a GET request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://restcountries.com/v3.1/all?fields=name,flags"))
                .GET()
                .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Print the status code and the response body
           // System.out.println("Status Code: " + response.statusCode());
           // System.out.println("Response Body: " + response.body());
            
            JsonArray arr = JsonParser.parseString(response.body()).getAsJsonArray();
            System.out.println(arr);
            for(int i = 0; i < arr.size(); i++) {
            	JsonObject obj = arr.get(i).getAsJsonObject();
            	obj = obj.get("name").getAsJsonObject();
            	String officeialName = obj.get("common").getAsString();
            	System.out.println(officeialName);
            }

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
