package com.aempactice.core.models;

import com.adobe.cq.wcm.core.components.models.Image;

public interface CustomImage extends Image {

    /**
     * Returns the custom image height configured via the {@code imageHeight}
     * JCR property on the component's content node.
     *
     * @return height in pixels; {@code 0} when not configured
     */
    int getImageHeight();
}
