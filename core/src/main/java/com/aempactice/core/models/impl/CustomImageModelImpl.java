package com.aempactice.core.models.impl;

import com.adobe.cq.wcm.core.components.models.Image;
import com.aempactice.core.models.CustomImage;

import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import javax.annotation.PostConstruct;

@Slf4j
@Model(
        adaptables = {SlingHttpServletRequest.class, Resource.class},
        adapters = {CustomImage.class, Image.class},
        resourceType = CustomImageModelImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CustomImageModelImpl implements CustomImage {

    /**
     * The project-specific resource type for this component.
     * Must match the node path under /apps.
     */
    public static final String RESOURCE_TYPE = "aempractice/components/image";

    @Self
    @Via(type = ResourceSuperType.class)
    @Delegate(excludes = DelegateExclusions.class)
    private Image delegate;

    @ValueMapValue(name = "imageHeight")
    @Default(intValues = 0)
    private int imageHeight;

    @PostConstruct
    protected void init() {
        log.debug("CustomImageModel initialised for resource type: {}", RESOURCE_TYPE);
        log.debug("imageHeight resolved to: {}", imageHeight);
    }

    /**
     * Returns the project-specific resource type instead of the Core one.
     *
     * @return {@value #RESOURCE_TYPE}
     */
    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }

    @Override
    public int getImageHeight() {
        return imageHeight;
    }

    private interface DelegateExclusions {
        String getExportedType();
    }
}