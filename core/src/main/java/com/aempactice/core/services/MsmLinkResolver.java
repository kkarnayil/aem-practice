package com.aempactice.core.services;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.ResourceResolver;

public interface MsmLinkResolver {
    
    /**
     * Resolves MSM link for localized pages.
     */
    String resolve(String targetPath, Page currentPage, ResourceResolver resolver);
    
    /**
     * Check if current page is on language-masters branch.
     */
    boolean isLanguageMaster(Page currentPage);
}

