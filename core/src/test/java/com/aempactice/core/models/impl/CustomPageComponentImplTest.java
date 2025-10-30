package com.aempactice.core.models.impl;

import com.aempactice.core.services.JsonSchemaGenerator;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.featureflags.Features;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomPageComponentImplTest {

    @InjectMocks
    private CustomPageComponentImpl customPageComponentImpl;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private Features features;

    @Mock
    private JsonSchemaGenerator jsonSchemaGenerator;

    @Mock
    private Page currentPage;

    @Mock
    private Resource resource;

    @Mock
    private ValueMap valueMap;

    @BeforeEach
    void setUp() {
        when(request.getResource()).thenReturn(resource);
        when(jsonSchemaGenerator.generateSchema(request, resource)).thenReturn("{\"test\":\"TestSchema\"}");


        when(currentPage.getContentResource()).thenReturn(resource);
        when(resource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("test", String.class)).thenReturn("OverriddenValue");
        when(valueMap.get("test2", String.class)).thenReturn("OverriddenValue222222222");
    }

    @Test
    void testJsonSchema() {
        customPageComponentImpl.init();
        Assertions.assertEquals("{\"test\":\"TestSchema\"}", customPageComponentImpl.getJsonSchema());
    }

}
