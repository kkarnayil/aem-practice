package com.aempactice.core.services.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.aempactice.core.beans.Country;
import com.aempactice.core.services.CountryService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component(service = CountryService.class, name = "v3")
public class CountryServiceImplV3 implements CountryService {

	@Override
	public List<Country> getCountryList() {
		List<Country> countryList = new ArrayList<>();

		HttpClient client = HttpClient.newHttpClient();

		// Create a GET request
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://restcountries.com/v3.1/all?fields=name,flags")).GET().build();

		// Send the request and get the response
		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			JsonArray arr = JsonParser.parseString(response.body()).getAsJsonArray();
			System.out.println(arr);
			for (int i = 0; i < arr.size(); i++) {
				JsonObject obj = arr.get(i).getAsJsonObject();
				obj = obj.get("name").getAsJsonObject();
				String officeialName = obj.get("common").getAsString();
				System.out.println(officeialName);
				Country country = new Country();
				country.setCountryName(officeialName);
				countryList.add(country);
			}

		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return countryList;

	}
}
