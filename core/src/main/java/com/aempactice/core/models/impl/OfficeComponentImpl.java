package com.aempactice.core.models.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ExporterConstants;
import com.aempactice.core.beans.Country;
import com.aempactice.core.models.OfficeComponent;
import com.aempactice.core.services.CountryService;
import com.day.cq.wcm.api.Page;

@Model(adaptables = Resource.class, adapters = {
		OfficeComponent.class }, resourceType = OfficeComponentImpl.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class OfficeComponentImpl implements OfficeComponent {

	public static final String RESOURCE_TYPE = "aempractice/components/officecomponent";

	@ValueMapValue
	private String heading;

	@ValueMapValue
	private String countryName;

	@SlingObject
	private Resource resource;

	@OSGiService(filter = "(component.name=v3)")
	private CountryService countryService;

	private List<Address> officeAddresses;
	
	List<String> pageTitles = new ArrayList<>();

	@PostConstruct
	private void init() {
		
	
		Resource officeAddress = resource.getChild("officeAddresses");
		if (null != officeAddress && officeAddress.hasChildren()) {
			Iterator<Resource> officeIterator = officeAddress.getChildren().iterator();
			officeAddresses = new ArrayList<>();
			while (officeIterator.hasNext()) {
				Resource addressResource = officeIterator.next();
				ValueMap addressMap = addressResource.getValueMap();

				String address = addressMap.get("address", String.class);
				String buildingName = addressMap.get("buildingName", String.class);
				String city = addressMap.get("city", String.class);
				String state = addressMap.get("state", String.class);
				String pincode = addressMap.get("pincode", String.class);
				boolean hideOffice = addressMap.get("hideOffice", true);

				Address addressObj = new Address();

				addressObj.setAddress(address);
				addressObj.setBuildingName(buildingName);
				addressObj.setCity(city);
				addressObj.setHideOffice(hideOffice);
				addressObj.setPincode(pincode);
				addressObj.setState(state);
				officeAddresses.add(addressObj);
			}
		}
	}


	@Override
	public List<Address> getOfficeAddresses() {
		return officeAddresses;
	}

	@Override
	public String getHeading() {
		if (null != heading) {
			return heading.toUpperCase();
		} else {
			return null;
		}
	}

	@Override
	public String getCountryName() {
		return countryName;
	}

	@Override
	public Map<String, Object> getAttributeMap() {
		Map<String, Object> attributeMap = new HashMap<>();
		attributeMap.put("data-api", "www.endjf.com");
		attributeMap.put("data-ref", "www.reff");
		attributeMap.put("data-test", true);
		attributeMap.put("data-test1", 2);
		attributeMap.put("data-test2", null);
		return attributeMap;
	}

	@Override
	public List<Country> getCountryList() {
		return countryService.getCountryList();
	}

}
