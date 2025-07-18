package com.aempactice.core.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.aempactice.core.beans.Country;
import com.aempactice.core.services.CountryService;

@Component(service = CountryService.class, name="v1")
public class CountryServiceImpl implements CountryService{

	@Override
	public List<Country> getCountryList() {
		List<Country> countryList = new ArrayList<>();
		Country country = new Country();
		country.setCountryName("India");
		countryList.add(country);
		return countryList;
	}
}
