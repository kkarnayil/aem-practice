package com.aempactice.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.aempactice.core.services.CountryService;
import com.drew.lang.annotations.NotNull;
import com.google.gson.Gson;

@Component(service = Servlet.class)
@SlingServletPaths(value = { "/bin/hello" })
public class CountryServlet extends SlingSafeMethodsServlet{
	private static final long serialVersionUID = 1L;

	@Reference(target = "(component.name=v2)")
	private CountryService countryService;
	
	@Override
	protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
			throws ServletException, IOException {
		Gson jsonString = new Gson();
		response.setHeader("content-type", "application/json");
		response.getWriter().write(jsonString.toJson(countryService.getCountryList()));
	}

}
