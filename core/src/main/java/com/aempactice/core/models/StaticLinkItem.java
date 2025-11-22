package com.aempactice.core.models;

import com.adobe.cq.wcm.core.components.models.ListItem;

public interface StaticLinkItem extends ListItem {
    String getHref();
    String getLinkTitle();
}
