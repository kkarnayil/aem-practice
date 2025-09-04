package com.aempactice.core.models.impl;

import com.adobe.cq.export.json.ExporterConstants;
import com.aempactice.core.models.CustomPageComponent;
import com.aempactice.core.services.JsonSchemaGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.featureflags.Features;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

@Slf4j
@Model(adaptables = {SlingHttpServletRequest.class}, adapters = {
        CustomPageComponent.class}, resourceType = CustomPageComponentImpl.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class CustomPageComponentImpl implements CustomPageComponent {

    public static final String RESOURCE_TYPE = "aempractice/components/page";

    @Self
    private SlingHttpServletRequest request;

    @ValueMapValue(name = "customFeature")
    private String customFeature;

    @OSGiService
    private Features features;

    @OSGiService
    private JsonSchemaGenerator jsonSchemaGenerator;

    private String jsonSchema;

    @PostConstruct
    void init(){
        if(request == null || null == jsonSchemaGenerator){
            log.info("No request or jsonSchemaGenerator provided : {}, {}", request, jsonSchemaGenerator);
            return; }

        log.info("Initializing CustomPageComponent");
        jsonSchema = jsonSchemaGenerator.generateSchema(request, request.getResource());
    }

    @Override
    public boolean isCustomFeatureEnabled() {
        return features.isEnabled("customFeature");
    }

    @Override
    public String getCustomFeature() {
        return customFeature;
    }

    @Override
    public String getJsonSchema() {
        return jsonSchema;
    }


}
