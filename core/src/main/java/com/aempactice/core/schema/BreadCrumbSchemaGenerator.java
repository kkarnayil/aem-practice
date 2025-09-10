package com.aempactice.core.schema;

import com.adobe.cq.wcm.core.components.models.Breadcrumb;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class BreadCrumbSchemaGenerator {

    @JsonIgnore
    private Breadcrumb breadcrumb;

    @JsonIgnore
    private Externalizer externalizer;

    @JsonIgnore
    private SlingHttpServletRequest request;

    @Getter
    @JsonProperty("@context")
    private String context = "https://schema.org";

    @Getter
    @JsonProperty("@type")
    private String type = "BreadcrumbList";

    @Getter
    @JsonProperty("itemListElement")
    final List<ListItem> itemListElements = new ArrayList<>();

    public BreadCrumbSchemaGenerator(Breadcrumb breadcrumb, Externalizer externalizer, SlingHttpServletRequest request) {
        this.breadcrumb = breadcrumb;
        this.externalizer = externalizer;
        this.request = request;
    }

    @JsonIgnore
    public BreadCrumbSchemaGenerator getSchema() {
        if (breadcrumb != null && breadcrumb.getItems() != null && !breadcrumb.getItems().isEmpty()) {
            int position = 1;
            for (NavigationItem item : breadcrumb.getItems()) {
                String link = item.getLink() != null ? externalizer.publishLink(request.getResourceResolver(), item.getLink().getURL())  : null;
                itemListElements.add(new ListItem(position, item.getTitle(), link));
                position++;
            }
        }
        return this;
    }
}
