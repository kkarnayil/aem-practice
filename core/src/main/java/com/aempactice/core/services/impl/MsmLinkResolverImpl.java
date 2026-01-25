package com.aempactice.core.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.aempactice.core.services.MsmLinkResolver;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import javax.jcr.RangeIterator;

@Slf4j
@Component(service = MsmLinkResolver.class, immediate = true)
public class MsmLinkResolverImpl implements MsmLinkResolver {

    @Reference
    private LiveRelationshipManager liveRelationshipManager;

    @Override
    public String resolve(String targetPath, Page currentPage, ResourceResolver resolver) {
        if (StringUtils.isBlank(targetPath) || currentPage == null || resolver == null) {
            log.debug("Invalid parameters for MSM resolution");
            return targetPath;
        }

        String currentPagePath = currentPage.getPath();

        // If we're on language-masters, return original path
        if (isLanguageMaster(currentPage)) {
            return targetPath;
        }

        // Handle anchors
        String actualTargetPath = targetPath;
        String anchor = null;
        if (targetPath.contains("#")) {
            String[] parts = targetPath.split("#");

            if(parts.length >  0) {
                actualTargetPath = parts[0];
            }
            if (parts.length > 1) {
                anchor = parts[1];
            }
        }

        Resource resource = resolver.getResource(actualTargetPath);
        if (resource == null) {
            log.debug("Target resource not found: {}", actualTargetPath);
            return targetPath;
        }

        try {
            RangeIterator iterator = liveRelationshipManager.getLiveRelationships(
                resource, currentPagePath, null);
            
            if (iterator.hasNext()) {
                LiveRelationship relationship = (LiveRelationship) iterator.next();
                String msmLink = relationship.getTargetPath();
                
                if (anchor != null) {
                    msmLink += "#" + anchor;
                }
                
                log.debug("Resolved MSM link: {} -> {}", targetPath, msmLink);
                return msmLink;
            }
        } catch (Exception e) {
            log.error("Error while resolving MSM link for: {}", targetPath, e);
        }

        return targetPath;
    }

    @Override
    public boolean isLanguageMaster(Page currentPage) {
        if (currentPage == null) {
            return false;
        }
        return currentPage.getPath().contains("language-masters");
    }
}
