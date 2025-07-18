package com.aempactice.core.models;

import java.util.List;
import java.util.Map;

import com.aempactice.core.beans.Country;
import com.aempactice.core.models.impl.Address;

public interface OfficeComponent {
	
	List<Address> getOfficeAddresses();
	
	String getHeading();
	
	String getCountryName();
	
	Map<String, Object> getAttributeMap();
	
	List<Country> getCountryList();
}
