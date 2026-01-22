package com.aempactice.core.utils;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RangeIterator;
import java.util.Optional;

@Slf4j
public class CommonUtils {

    private CommonUtils() {
    }

    /**
     * Safely reads Content Fragment element value
     */
    public static String getFragmentValue(ContentFragment fragment, String elementName) {

        if (fragment == null || !fragment.hasElement(elementName)) {
            return StringUtils.EMPTY;
        }

        return Optional.ofNullable(fragment.getElement(elementName).getValue())
                .map(v -> v.getValue(String.class))
                .orElse(StringUtils.EMPTY);
    }

    public static String getMsmLink(String target, ResourceResolver resourceResolver, LiveRelationshipManager liveRelationshipManager, Page currentPage) {
        String courseMsmLink = null;

        if (StringUtils.isBlank(target)) {
            return null;
        }

        String currentPagePath = currentPage.getPath();

        if (currentPagePath.contains("language-masters")) {
            return target;
        }

        String targetPath = target;
        String anchor = null;
        if (target.contains("#")) {
            String[] parts = target.split("#");
            targetPath = parts[0];
            log.debug("Path after split: {}", targetPath);
            if(parts.length > 1) {
                anchor = parts[1];
                log.debug("anchor: {}", anchor);
            }
        }

        Resource resource = resourceResolver.getResource(targetPath);
        if (resource == null) {
            return null;
        }

        try {
            RangeIterator iterator = liveRelationshipManager.getLiveRelationships(resource, currentPagePath, null);
            if (iterator.hasNext()) {
                LiveRelationship relationship = (LiveRelationship) iterator.next();
                courseMsmLink = relationship.getTargetPath();
                if(anchor != null) {
                    courseMsmLink += "#"+anchor;
                }
            }
        } catch (Exception e) {
            log.error("Error while getting MSM link", e);
        }

        return courseMsmLink;
    }
}
