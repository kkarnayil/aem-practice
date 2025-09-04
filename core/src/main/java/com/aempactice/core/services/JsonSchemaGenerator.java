package com.aempactice.core.services;


import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

public interface JsonSchemaGenerator {

    default String generateSchema(SlingHttpServletRequest request, Resource resource) {
        return "{}";
    }
}
