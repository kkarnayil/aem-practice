package com.aempactice.core.models;

import com.adobe.cq.wcm.core.components.internal.models.v3.ListImpl;
import com.adobe.cq.wcm.core.components.testing.mock.ContextPlugins;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ExtendWith(AemContextExtension.class)
public class ExtendedListImplTest {

    private final AemContext context = new AemContextBuilder(ResourceResolverType.JCR_MOCK)
            .plugin(ContextPlugins.CORE_COMPONENTS)
            .build();

    @BeforeEach
    public void setUp() {
        context.load().json("/json/page.json", "/content");
        context.currentResource("/content/test-page/jcr:content/list");
        context.addModelsForClasses(ListImpl.class);
    }

    @Test
    public void testListStaticOption() {
        ModelFactory modelFactory = context.getService(ModelFactory.class);
        ExtendedListImpl list = Objects.requireNonNull(modelFactory)
                .createModel(context.request(), ExtendedListImpl.class);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(ExtendedListImpl.RESOURCE_TYPE, list.getExportedType());
        Assertions.assertEquals(4, list.getListItems().size());

        List<String> titles = list.getListItems().stream()
                .map(listItem -> ((StaticLinkItem) listItem).getLinkTitle())
                .collect(Collectors.toList());
        List<String> urls = list.getListItems().stream()
                .map(listItem -> ((StaticLinkItem) listItem).getHref())
                .collect(Collectors.toList());

        Assertions.assertEquals(List.of("External Site", "Override", "Page Title", "Navigation Title"), titles);
        Assertions.assertEquals(List.of("https://www.externalsite.com", "/content/override.html", "/content/pagetitle.html", "/content/navtitle.html"), urls);
    }

    @Test
    public void test_Empty_List(){
        context.currentResource("/content/test-page/jcr:content/emptyList");
        ModelFactory modelFactory = context.getService(ModelFactory.class);
        ExtendedListImpl list = Objects.requireNonNull(modelFactory)
                .createModel(context.request(), ExtendedListImpl.class);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(ExtendedListImpl.RESOURCE_TYPE, list.getExportedType());
        Assertions.assertEquals(0, list.getListItems().size());
    }
}
