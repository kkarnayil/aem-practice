package com.aempactice.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;

import com.aempactice.core.models.OfficeComponent;
import com.drew.lang.annotations.NotNull;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Component(service = Servlet.class)
@SlingServletResourceTypes(resourceTypes = {
		"aempractice/components/officecomponent" }, selectors = {"v2", "v3"}, extensions = "json")
public class CountryServlet3 extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
			throws ServletException, IOException {
		Gson jsonString = new Gson();
		Resource rs = request.getResource();
		OfficeComponent component = rs.adaptTo(OfficeComponent.class);
		
		String selec = request.getRequestPathInfo().getSelectorString();
		
		JsonObject object = new JsonObject();
		if(selec.equalsIgnoreCase("v2")) {
			object.addProperty("name", "Kartik");
		}else {
			object.addProperty("name", "Ninila");
		}
		object.add("countries", jsonString.toJsonTree(component.getCountryList()));
		object.add("offices", jsonString.toJsonTree(component.getOfficeAddresses()));
		object.add("attributes", jsonString.toJsonTree(component.getAttributeMap()));

		response.setHeader("content-type", "application/json");
		response.getWriter().write(object.toString());
	}

}
