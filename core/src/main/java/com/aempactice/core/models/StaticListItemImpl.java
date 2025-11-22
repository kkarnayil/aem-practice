package com.aempactice.core.models;

public class StaticListItemImpl implements StaticLinkItem {

    private final String title;
    private final String href;

    public StaticListItemImpl(String title, String href) {
        this.title = title;
        this.href = href;
    }

    @Override
    public String getHref() {
        return this.href;
    }

    @Override
    public String getLinkTitle() {
        return this.title;
    }
}
