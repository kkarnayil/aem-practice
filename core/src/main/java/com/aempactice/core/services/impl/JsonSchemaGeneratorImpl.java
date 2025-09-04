package com.aempactice.core.services.impl;

import com.adobe.cq.wcm.core.components.models.Breadcrumb;
import com.aempactice.core.schema.BreadCrumbSchemaGenerator;
import com.aempactice.core.services.JsonSchemaGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component(service = JsonSchemaGenerator.class)
public class JsonSchemaGeneratorImpl implements JsonSchemaGenerator {

    @Reference
    private ModelFactory modelFactory;

    SlingHttpServletRequest request;

    class CollectionData {
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
    }

    @Override
    public String generateSchema(SlingHttpServletRequest request, Resource resource) {
        log.info("Generating schema for resource {}", resource.getPath());
        CollectionData collectionData = new CollectionData();
        this.request = request;
        collectionData = collectComponentData(collectionData, resource);
        log.info("Data Collected {}", collectionData.breadcrumbs.get(0).getItems().size());
        return generateJsonSchema(collectionData);
    }

    private CollectionData collectComponentData(CollectionData collectionData, Resource resource) {
        try {
            if (resource != null) {
                if ("aempractice/components/breadcrumb".equals(resource.getResourceType())) {
                    log.info("Found Breadcrumb component at path: {}", resource.getPath());
                    Breadcrumb breadcrumb = modelFactory.getModelFromWrappedRequest(request, resource, Breadcrumb.class);

                    if (breadcrumb != null) {
                        log.info("Breadcrumb Sling model created");
                        collectionData.breadcrumbs.add(breadcrumb);
                    } else {
                        log.warn("Could not adapt resource at path {} to Breadcrumb", resource.getPath());
                    }
                }
                if (resource.hasChildren()) {
                    for (Resource childResource : resource.getChildren()) {
                        collectionData = collectComponentData(collectionData, childResource);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return collectionData;
    }

    private String generateJsonSchema(CollectionData collectionData) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        try {
            if (collectionData.breadcrumbs == null || collectionData.breadcrumbs.isEmpty()) {
                return "{}";
            } else {
                for (Breadcrumb breadcrumb : collectionData.breadcrumbs) {
                    JsonNode node = null;
                    node = mapper.readTree(new BreadCrumbSchemaGenerator(breadcrumb).getSchema());
                    arrayNode.add(node);
                }
            }
            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
            log.info("Generated JSON Schema: {}", jsonString);
            return jsonString;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
