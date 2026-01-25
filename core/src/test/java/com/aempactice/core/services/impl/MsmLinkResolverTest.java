package com.aempactice.core.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.RangeIterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class MsmLinkResolverTest {

    @Mock
    LiveRelationshipManager liveRelationshipManager;

    @InjectMocks
    private MsmLinkResolverImpl msmLinkResolver;

    private final AemContext context = new AemContextBuilder()
            .build();

    @BeforeEach
    public void setUp() {
        context.registerService(LiveRelationshipManager.class, liveRelationshipManager);
        context.load().json("/json/course-lessons-journey.json", "/content");
    }

    @Test
    void testResolveLanguageMasterPage() {
        Page page = context.currentPage("/content/mccom/language-masters/en/courses");
        String targetPage = msmLinkResolver.resolve("/content/mccom/language-masters/en/courses/course-1", page, context.resourceResolver());
        assertEquals("/content/mccom/language-masters/en/courses/course-1", targetPage);
    }

    @Test
    void testResolveInvalidResourse() {
        Page page = context.currentPage("/content/mccom/us/en/courses");
        String targetPage = msmLinkResolver.resolve("/content/mccom/us/en/courses/course-invalid", page, context.resourceResolver());
        assertEquals("/content/mccom/us/en/courses/course-invalid", targetPage);
    }

    @Test
    void testResolveNull() {
        String targetPage = msmLinkResolver.resolve(null, null, null);
        assertNull(targetPage);

        targetPage = msmLinkResolver.resolve("#", null, null);
        assertEquals("#", targetPage);
    }

    @Test
    void testResolveUsPageException() throws WCMException {
        Page page = context.currentPage("/content/mccom/us/en/courses");
        when(liveRelationshipManager.getLiveRelationships(any(Resource.class), anyString(), any())).thenThrow(new WCMException("Test Exception"));
        String targetPage = msmLinkResolver.resolve("/content/mccom/us/en/courses/course-1", page, context.resourceResolver());
        assertEquals("/content/mccom/us/en/courses/course-1", targetPage);
    }

    @Test
    void testResolveUsPage() throws WCMException {
        Page page = context.currentPage("/content/mccom/us/en/courses");
        RangeIterator iterator = Mockito.mock(RangeIterator.class);
        LiveRelationship relationship = Mockito.mock(LiveRelationship.class);
        when(relationship.getTargetPath()).thenReturn("/content/mccom/us/en/courses/course-1");
        when(liveRelationshipManager.getLiveRelationships(any(Resource.class), anyString(), any())).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true).thenReturn(false);
        when(iterator.next()).thenReturn(relationship);

        String targetPage = msmLinkResolver.resolve("/content/mccom/language-masters/en/courses/course-1", page, context.resourceResolver());
        assertEquals("/content/mccom/us/en/courses/course-1", targetPage);
    }

    @Test
    void testResolveUsPageWithAnchor() throws WCMException {
        Page page = context.currentPage("/content/mccom/us/en/courses");
        RangeIterator iterator = Mockito.mock(RangeIterator.class);
        LiveRelationship relationship = Mockito.mock(LiveRelationship.class);
        when(relationship.getTargetPath()).thenReturn("/content/mccom/us/en/courses");
        when(liveRelationshipManager.getLiveRelationships(any(Resource.class), anyString(), any())).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true).thenReturn(false);
        when(iterator.next()).thenReturn(relationship);

        String targetPage = msmLinkResolver.resolve("/content/mccom/language-masters/en/courses#course-1", page, context.resourceResolver());
        assertEquals("/content/mccom/us/en/courses#course-1", targetPage);
    }

    @Test
    void testIsLanguageMasterNull() {
        assertFalse(msmLinkResolver.isLanguageMaster(null));
    }

    @Test
    void testIsLanguageMaster() {
        Page page = context.currentPage("/content/mccom/language-masters/en/courses/course-1");
        assertTrue(msmLinkResolver.isLanguageMaster(page));
    }

    @Test
    void testIsLanguageMasterFalse() {
        Page page = context.currentPage("/content/mccom/us/en/courses/course-1");
        assertFalse(msmLinkResolver.isLanguageMaster(page));
    }
}
