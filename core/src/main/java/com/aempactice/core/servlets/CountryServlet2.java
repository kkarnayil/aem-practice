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
import org.osgi.service.component.annotations.Reference;

import com.aempactice.core.services.CountryService;
import com.drew.lang.annotations.NotNull;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Component(service = Servlet.class)
@SlingServletResourceTypes(resourceTypes = { "aempractice/components/officecomponent" }, extensions = "json")
public class CountryServlet2 extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = 1L;

	@Reference(target = "(component.name=v2)")
	private CountryService countryService;

	@Override
	protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
			throws ServletException, IOException {
		Gson jsonString = new Gson();
		Resource rs = request.getResource();
		//OfficeComponent component = rs.adaptTo(OfficeComponent.class);
		
		String countryName = rs.getValueMap().get("countryName",String.class);
		
		JsonObject object = new JsonObject();
		object.add("countries", jsonString.toJsonTree(countryService.getCountryList()));
		object.addProperty("cn", countryName);
		
		response.setHeader("content-type", "application/json");
		response.getWriter().write(object.toString());
	}

}
