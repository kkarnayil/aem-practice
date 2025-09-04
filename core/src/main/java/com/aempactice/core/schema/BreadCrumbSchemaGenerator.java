package com.aempactice.core.schema;

import com.adobe.cq.wcm.core.components.models.Breadcrumb;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class BreadCrumbSchemaGenerator {

    @JsonIgnore
    private Breadcrumb breadcrumb;

    @JsonProperty("@context")
    private final String context = "https://schema.org";

    @JsonProperty("@type")
    private final String type = "BreadcrumbList";

    @JsonProperty("itemListElement")
    final List<ListItem> itemListElements = new ArrayList<>();

    @JsonIgnore
    public String getSchema() {
        if (breadcrumb != null && breadcrumb.getItems() != null && !breadcrumb.getItems().isEmpty()) {
            int position = 1;
            for (NavigationItem item : breadcrumb.getItems()) {

                itemListElements.add(new ListItem(position, item.getTitle(), item.getLink() != null && !item.isCurrent() ? item.getLink().getURL() : null));
                position++;
            }
            try {
                return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writerWithDefaultPrettyPrinter().writeValueAsString(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
