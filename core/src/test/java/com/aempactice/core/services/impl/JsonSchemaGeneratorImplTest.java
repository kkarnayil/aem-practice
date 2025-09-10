package com.aempactice.core.services.impl;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.Breadcrumb;

import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.aempactice.core.schema.BreadCrumbSchemaGenerator;
import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JsonSchemaGeneratorImplTest {

    private JsonSchemaGeneratorImpl service;
    private Resource resource;
    private SlingHttpServletRequest request;


    @BeforeEach
    void setUp() {
        service = new JsonSchemaGeneratorImpl();
        request = mock(SlingHttpServletRequest.class);
        resource = mock(Resource.class);
    }


    @Test
    void testGenerateJsonLd_Breadcrumb() throws IllegalAccessException, JsonProcessingException {

        Resource breadCrumbResource = mock(Resource.class);
        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        ModelFactory modelFactory = mock(ModelFactory.class);
        Externalizer externalizer = mock(Externalizer.class);
        Breadcrumb breadcrumb = mock(Breadcrumb.class);
        NavigationItem item = mock(NavigationItem.class);
        Link link = mock(Link.class);

        // set private fields
        FieldUtils.writeField(service, "modelFactory", modelFactory, true);
        FieldUtils.writeField(service, "externalizer", externalizer, true);
        when(modelFactory.getModelFromWrappedRequest(any(SlingHttpServletRequest.class), any(Resource.class), eq(Breadcrumb.class))).thenReturn(breadcrumb);
        when(externalizer.publishLink(any(), any())).thenReturn("http://localhost:4503/content/test.html");

        Iterator<Resource> children = java.util.Arrays.asList(breadCrumbResource).iterator();
        when(resource.getChildren()).thenReturn(() -> children);

        when(breadCrumbResource.getResourceType()).thenReturn("aempractice/components/breadcrumb");
        when(link.getURL()).thenReturn("/content/test");
        when(item.getLink()).thenReturn(link);
        when(item.getTitle()).thenReturn("Test Title");
        when(breadcrumb.getItems()).thenReturn(java.util.Arrays.asList(item));

        String result = service.generateSchema(request, breadCrumbResource);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode arrayNode = mapper.readTree(result);
        JsonNode firstObjectNode = arrayNode.get(0);
        System.out.println(firstObjectNode.toString());
        BreadCrumbSchemaGenerator generator = new ObjectMapper().treeToValue(firstObjectNode, BreadCrumbSchemaGenerator.class);
        assertEquals("https://schema.org", generator.getContext());
        assertEquals("BreadcrumbList", generator.getType());
        assertEquals(1, generator.getItemListElements().size());
        assertEquals("http://localhost:4503/content/test.html", generator.getItemListElements().get(0).getItem());
        assertEquals("Test Title", generator.getItemListElements().get(0).getName());
        assertEquals("ListItem", generator.getItemListElements().get(0).getType());

    }


}
