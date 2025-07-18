package com.aempactice.core.models.impl;

public class Address {
	
	private String address;
	private String buildingName;
	private String city;
	private String state;
	private String pincode;
	private boolean hideOffice;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBuildingName() {
		return buildingName;
	}
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	public boolean isHideOffice() {
		return hideOffice;
	}
	public void setHideOffice(boolean hideOffice) {
		this.hideOffice = hideOffice;
	}
	
}
