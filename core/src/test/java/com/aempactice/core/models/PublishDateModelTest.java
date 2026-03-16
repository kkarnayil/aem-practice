package com.aempactice.core.models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class PublishDateModelTest {

    private final AemContext context = new AemContext();

    private static final String PAGE_PATH = "/content/test-page";

    @BeforeEach
    void setUp() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.DECEMBER, 18, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        context.create().page(PAGE_PATH);
        context.pageManager().getPage(PAGE_PATH).getContentResource().adaptTo(ModifiableValueMap.class).put("originalPublishDate", calendar);
        context.currentPage(PAGE_PATH);
    }


    @Test
    void testFormattedDate() {
        PublishDateModel model = context.request().adaptTo(PublishDateModel.class);
        assertNotNull(model);
        assertEquals("December 18, 2025", model.getPublishDate());
    }

    @Test
    void testNullDateProperty() {

        String pagePath = "/content/page-no-date";
        context.create().page(pagePath);
        context.currentPage(pagePath);
        PublishDateModel model = context.request().adaptTo(PublishDateModel.class);
        assertNotNull(model);
        assertNull(model.getPublishDate());
    }
}