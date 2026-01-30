package com.aempactice.core.models;

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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class CourseModelTest {

    private CourseModelImpl courseModel;

    @Mock
    private CourseService courseService;

    @Mock
    private MsmLinkResolver msmLinkResolver;

    private final AemContext context = new AemContextBuilder().build();

    @BeforeEach
    public void setUp() {
        context.registerService(CourseService.class, courseService);
        context.registerService(MsmLinkResolver.class, msmLinkResolver);
        context.addModelsForClasses(CourseModelImpl.class);
        context.load().json("/json/course-lessons-journey.json", "/content");
    }

    @Test
    void testCourseModelValid() {

        context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course");
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

        when(courseService.getCourse(anyString(), any(ResourceResolver.class))).thenReturn(mockCourse);
        when(msmLinkResolver.resolve(anyString(), any(Page.class), any(ResourceResolver.class))).thenReturn("/msm/link/course-1");

        courseModel = context.request().adaptTo(CourseModelImpl.class);
        assertNotNull(courseModel);
        Course course = courseModel.getCourse();
        assertAll(
                () -> assertEquals("course-1", course.getId()),
                () -> assertEquals("Test Title", course.getTitle()),
                () -> assertEquals("test.png", course.getThumbnail()),
                () -> assertEquals(1, course.getLessonCount()),
                () -> assertEquals("/msm/link/course-1", course.getPath()),
                () -> assertEquals("View Course", courseModel.getViewCourseLinkLabel())
        );

    }

    @Test
    void testServiceCourseNull() {
        context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course");
        when(courseService.getCourse(anyString(), any(ResourceResolver.class))).thenReturn(null);
        courseModel = context.request().adaptTo(CourseModelImpl.class);
        assertNotNull(courseModel);
        assertNull(courseModel.getCourse());
    }

    @Test
    void testServiceCourseUnConfigured() {
        context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-unconfigured");
        courseModel = context.request().adaptTo(CourseModelImpl.class);
        assertNotNull(courseModel);
        assertNull(courseModel.getCourse());
    }
}
