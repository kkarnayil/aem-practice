package com.aempactice.core.models;

import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = {List.class},
        resourceType = ExtendedListImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
        name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
        extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
@Slf4j
public class ExtendedListImpl implements List {

    protected static final String RESOURCE_TYPE = "aempractice/components/list";

    private static final String PV_CUSTOM_LINK = "customstatic";

    @ValueMapValue
    private String listFrom;

    @Getter
    private boolean isCustomList;

    @Self
    @Via(type = ResourceSuperType.class)
    @Delegate(excludes = DelegationExclusion.class)
    private List coreList;

    @Self
    private SlingHttpServletRequest request;

    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }

    @Override
    public Collection<ListItem> getListItems() {
        if (PV_CUSTOM_LINK.equals(listFrom)) {
            this.isCustomList = true;
            log.debug("Using custom static links for list items.");
            return getCustomListItems();
        }
        return coreList.getListItems();
    }

    private Collection<ListItem> getCustomListItems() {

        Optional<Resource> linkMultifieldOptional = Optional.ofNullable(request)
                .map(SlingHttpServletRequest::getResource)
                .filter(Resource::hasChildren)
                .map(r -> r.getChild(PV_CUSTOM_LINK));


        if (linkMultifieldOptional.isEmpty()) {
            log.debug("No '{}' multifield found or it has no children.", PV_CUSTOM_LINK);
            return java.util.List.of();
        }

        Resource multifieldResource = linkMultifieldOptional.get();
        Collection<ListItem> items = StreamSupport.stream(multifieldResource.getChildren().spliterator(), false)
                .map(child -> {
                    ValueMap vm = child.getValueMap();
                    String link = vm.get("linkURL", String.class);
                    String text = vm.get("linkText", String.class);
                    return new StaticListItemImpl(text, link);
                })
                .collect(Collectors.toList());

        log.debug("Retrieved {} custom static list items.", items.size());
        return java.util.List.copyOf(items);
    }

    private interface DelegationExclusion {
        Collection<ListItem> getListItems();
        String getResourceType();
    }
}
