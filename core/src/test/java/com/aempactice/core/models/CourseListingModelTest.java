package com.aempactice.core.models;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.aempactice.core.services.CourseService;
import com.aempactice.core.services.MsmLinkResolver;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class CourseListingModelTest {

    private CourseListingModelImpl courseListingModel;

    @Mock
    private CourseService courseService;

    @Mock
    private MsmLinkResolver msmLinkResolver;

    private final AemContext context = new AemContextBuilder().build();

    @BeforeEach
    public void setUp() {
        context.registerService(CourseService.class, courseService);
        context.registerService(MsmLinkResolver.class, msmLinkResolver);
        context.addModelsForClasses(CourseListingModelImpl.class);
        context.load().json("/json/course-lessons-journey.json", "/content");
    }

    @Test
    void testCourseListingUnconfigured() {
        context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-listing-unconfigured");
        courseListingModel = context.request().adaptTo(CourseListingModelImpl.class);
        assertNotNull(courseListingModel);
        assertEquals(0, courseListingModel.getCourses().size());
    }

    @Test
    void testCourseListingInvalidPath() {
        context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-listing-wrong-path");
        courseListingModel = context.request().adaptTo(CourseListingModelImpl.class);
        assertNotNull(courseListingModel);
        assertEquals(0, courseListingModel.getCourses().size());
    }

    @Test
    void testCourseListingEmptyFolder() {
        context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-listing-empty-folder");
        courseListingModel = context.request().adaptTo(CourseListingModelImpl.class);
        assertNotNull(courseListingModel);
        assertEquals(0, courseListingModel.getCourses().size());
    }

    @Test
    void testCoursesSuccess() {
        context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-listing");
        Lesson mockLesson = Lesson.builder()
                .id("lesson-1")
                .title("Test Lesson")
                .path("#")
                .thumbnail("test.png")
                .build();

        Course mockCourse = Course.builder()
                .id("course-1")
                .title("Test Title")
                .path("")
                .thumbnail("test.png")
                .lessons(List.of(mockLesson))
                .lessonCount(1)
                .build();

        when(courseService.isCourseFragment(any(ContentFragment.class))).thenReturn(true);
        when(courseService.getCourse(anyString(), any(ResourceResolver.class))).thenReturn(mockCourse);
        when(msmLinkResolver.resolve(anyString(), any(Page.class), any(ResourceResolver.class))).thenReturn("/msm/link/course-1");

        courseListingModel = context.request().adaptTo(CourseListingModelImpl.class);
        assertNotNull(courseListingModel);
        Course course = courseListingModel.getCourses().get(0);
        assertAll(
                () -> assertEquals("course-1", course.getId()),
                () -> assertEquals("Test Title", course.getTitle()),
                () -> assertEquals("test.png", course.getThumbnail()),
                () -> assertEquals(1, course.getLessonCount()),
                () -> assertEquals("/msm/link/course-1", course.getPath()),
                () -> assertEquals("View Course", courseListingModel.getViewCourseLinkLabel())
        );
    }

    @Test
    void testCFNotCourse() {
        context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-listing");
        when(courseService.isCourseFragment(any(ContentFragment.class))).thenReturn(false);
        courseListingModel = context.request().adaptTo(CourseListingModelImpl.class);
        assertNotNull(courseListingModel);
        assertEquals(0, courseListingModel.getCourses().size());
    }

    @Test
    void testServiceCourseNull() {
        context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-listing");
        when(courseService.isCourseFragment(any(ContentFragment.class))).thenReturn(true);
        when(courseService.getCourse(anyString(), any(ResourceResolver.class))).thenReturn(null);
        courseListingModel = context.request().adaptTo(CourseListingModelImpl.class);
        assertNotNull(courseListingModel);
        assertEquals(0, courseListingModel.getCourses().size());
    }
}
