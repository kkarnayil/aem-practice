package com.aempactice.core.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component
public class TestCompont{

	@Activate
	public void getCountryList() {
		System.out.println("Test");
	}
		
}