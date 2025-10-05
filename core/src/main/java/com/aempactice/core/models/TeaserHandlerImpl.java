package com.aempactice.core.models;

import com.adobe.cq.wcm.style.ComponentStyleInfo;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Model(adaptables = Resource.class)
public class TeaserHandlerImpl {

    @Self
    private Resource resource;

    @Getter
    private boolean isContainerClassSelected;

    @Getter
    private boolean isImageVariationSelected;

    @PostConstruct
    private void init(){
        String appliedCssClasses = getAppliedCssClasses();
        if(StringUtils.isNotBlank(appliedCssClasses)){
            isContainerClassSelected = StringUtils.containsIgnoreCase(appliedCssClasses, "style1");
            isImageVariationSelected = StringUtils.containsIgnoreCase(appliedCssClasses, "style2");
        }
    }

    private String getAppliedCssClasses() {
        return Optional.ofNullable(this.resource.adaptTo(ComponentStyleInfo.class))
                .map(ComponentStyleInfo::getAppliedCssClasses)
                .filter(StringUtils::isNotBlank)
                .orElse(null);
    }
}
