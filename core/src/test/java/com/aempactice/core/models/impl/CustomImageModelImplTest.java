package com.aempactice.core.models.impl;


import com.aempactice.core.models.CustomImage;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.adobe.cq.wcm.core.components.testing.mock.ContextPlugins.CORE_COMPONENTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CustomImageModel}.
 *
 * <p>Uses the official {@code CORE_COMPONENTS} AEM mock plugin from Adobe
 * ({@code com.adobe.cq:core.wcm.components.testing.aem-mock-plugin:2.27.0}).
 * The plugin auto-registers all OSGi services that Core Component models
 * depend on, so no manual Mockito wiring is needed for the Core delegate.
 * The real Core Image v2 model is resolved via ResourceSuperType — exactly
 * as it would be at runtime.</p>
 */
@ExtendWith(AemContextExtension.class)
class CustomImageModelTest {

    /**
     * AemContext built with the CORE_COMPONENTS plugin.
     * This single line replaces all the OSGi service registration boilerplate
     * that would otherwise be required for Core Component model tests.
     */
    private final AemContext ctx = new AemContextBuilder()
            .plugin(CORE_COMPONENTS)
            .build();

    private CustomImage underTest;

    @BeforeEach
    void setUp() {
        // Register our model so the context can adapt to it
        ctx.addModelsForClasses(CustomImageModelImpl.class);

        // Load test content — includes a page structure so Core Image resolves correctly
        ctx.load().json("/json/customImage.json", "/content");

        // Set the current page and resource (required for Core Image v2 internals)
        ctx.currentPage("/content/mypage");
        ctx.currentResource("/content/mypage/jcr:content/image");

        underTest = ctx.request().adaptTo(CustomImage.class);
        assertNotNull(underTest, "Model must adapt from SlingHttpServletRequest as CustomImage");
    }

    @Test
    @DisplayName("getResourceType() returns project-specific resource type")
    void getResourceType_returnsProjectType() {
        assertEquals("aempractice/components/image", underTest.getExportedType());
    }

    @Test
    @DisplayName("getImageHeight() returns value set in JCR")
    void getImageHeight_returnsConfiguredValue() {
        assertEquals(480, underTest.getImageHeight());
    }

    @Test
    @DisplayName("getImageHeight() defaults to 0 when property is absent")
    void getImageHeight_defaultsToZero() {
        ctx.currentResource("/content/mypage/jcr:content/image-no-height");
        CustomImage model = ctx.request().adaptTo(CustomImage.class);
        assertNotNull(model);
        assertEquals(0, model.getImageHeight());
    }

    @Test
    @DisplayName("getAlt() is delegated to Core Image and returns configured alt text")
    void getAlt_delegatesToCore() {
        assertEquals("Sample image", underTest.getAlt());
    }

}