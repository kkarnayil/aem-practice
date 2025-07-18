/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aempactice.core.servlets;

import com.day.cq.commons.jcr.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(
        resourceTypes="aempractice/components/teams",
        methods=HttpConstants.METHOD_GET,
        extensions="json")
@ServiceDescription("Teams Demo Servlet")
public class TeamsServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {

        int page = 1;
        int size = 10;
        try { page = Integer.parseInt(req.getParameter("page")); } catch (Exception ignored) {}
        try { size = Integer.parseInt(req.getParameter("size")); } catch (Exception ignored) {}


        // Hardcoded teams data
        List<JSONObject> teams = Arrays.asList(
                new JSONObject()
                        .put("title", "Team Alpha")
                        .put("description", "Description for Team Alpha")
                        .put("flagImage", "https://picsum.photos/200/300"),
                new JSONObject()
                        .put("title", "Team Beta")
                        .put("description", "Description for Team Beta")
                        .put("flagImage", "https://picsum.photos/200/300"),
                new JSONObject()
                        .put("title", "Team Gamma")
                        .put("description", "Description for Team Gamma")
                        .put("flagImage", "https://picsum.photos/200/300"),
                new JSONObject()
                        .put("title", "Team Marvel")
                        .put("description", "Description for Team Marvel")
                        .put("flagImage", "https://picsum.photos/200/300"),
                new JSONObject()
                        .put("title", "Team Wolfpack")
                        .put("description", "Description for Team Wolfpack")
                        .put("flagImage", "https://picsum.photos/200/300"),
                new JSONObject()
                        .put("title", "Team Avengers")
                        .put("description", "Description for Team Avengers")
                        .put("flagImage", "https://picsum.photos/200/300")

                // Add more teams as needed
        );

        int totalSize = teams.size();
        int totalPages = (int) Math.ceil((double) totalSize / size);
        int start = Math.max(0, (page - 1) * size);
        int end = Math.min(start + size, totalSize);

        JSONArray teamsArray = new JSONArray();
        for (int i = start; i < end; i++) {
            teamsArray.put(teams.get(i));
        }

        JSONObject result = new JSONObject();
        result.put("teams", teamsArray);
        result.put("totalSize", totalSize);
        result.put("currentPage", page);
        result.put("totalPages", totalPages);

        resp.setContentType("application/json");
        resp.getWriter().write(result.toString());
    }
}
