package com.aempactice.core.models;

import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import java.util.Collection;
import java.util.Objects;
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
    public static final String PN_LINK_URL = "linkURL";

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

    @SlingObject
    private ResourceResolver resourceResolver;

    @Self
    protected LinkManager linkManager;

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
            return java.util.List.of();
        }

        Resource multifieldResource = linkMultifieldOptional.get();
        Collection<ListItem> items = StreamSupport.stream(multifieldResource.getChildren().spliterator(), false)
                .map(this::formStaticLink)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.debug("Retrieved {} custom static list items.", items.size());
        return java.util.List.copyOf(items);
    }

    private StaticListItemImpl formStaticLink(Resource linkResource) {
        ValueMap vm = linkResource.getValueMap();
        String linkValue = vm.get(PN_LINK_URL, String.class);
        String textValue = vm.get("linkText", String.class);
        if(linkValue == null){
            return null;
        }

        if (isExternalLink(linkValue)) {
            Link link = linkManager.get(linkValue).build();
            if(link.isValid()) {
                return new StaticListItemImpl(textValue, linkValue);
            }
        } else {
            Resource resource = resourceResolver.getResource(linkValue);
            if (null != resource) {
                if (resource.adaptTo(Page.class) != null) {
                    Page page = resource.adaptTo(Page.class);
                    Link link = linkManager.get(page).build();
                    textValue = textValue == null ? getPageTitle(page) : textValue;
                    linkValue = link.getURL();
                    return new StaticListItemImpl(textValue, linkValue);
                }
            }
        }
        return null;
    }

    private String getPageTitle(Page page) {
        if(page.getNavigationTitle() != null){
            return page.getNavigationTitle();
        } else if(page.getTitle() != null){
            return page.getTitle();
        } else {
            return page.getName();
        }
    }

    public static boolean isExternalLink(String url) {
        return StringUtils.isNotEmpty(url) && !url.startsWith("/");
    }

    private interface DelegationExclusion {
        Collection<ListItem> getListItems();
        String getResourceType();
    }
}
