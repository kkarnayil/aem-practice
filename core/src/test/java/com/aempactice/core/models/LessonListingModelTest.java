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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class LessonListingModelTest {

    private LessonListingModel lessonListingModel;

    @Mock
    private CourseService courseService;

    @Mock
    private MsmLinkResolver msmLinkResolver;

    private final AemContext context = new AemContextBuilder()
            .build();

    @BeforeEach
    public void setUp() {
        context.registerService(CourseService.class, courseService);
        context.registerService(MsmLinkResolver.class, msmLinkResolver);
        context.addModelsForClasses(LessonListingModel.class);
        context.load().json("/json/course-lessons-journey.json", "/content");
    }

    @Test
    void testLessonListingUnconfigured() {
        context.currentResource("/content/mccom/language-masters/en/lessons/jcr:content/root/lesson-listing-unconfigured");
        lessonListingModel = context.request().adaptTo(LessonListingModel.class);
        assertNotNull(lessonListingModel);
        assertEquals(0, lessonListingModel.getLessons().size());
    }

    @Test
    void testCourseListingInvalidPath() {
        context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-listing-wrong-path");
        lessonListingModel = context.request().adaptTo(LessonListingModel.class);
        assertNotNull(lessonListingModel);
        assertEquals(0, lessonListingModel.getLessons().size());
    }

    @Test
    void testNullCourse() {
        context.currentResource("/content/mccom/language-masters/en/lessons/jcr:content/root/lesson-listing");
        when(courseService.getCourse(anyString(), any(ResourceResolver.class))).thenReturn(null);
        lessonListingModel = context.request().adaptTo(LessonListingModel.class);
        assertNotNull(lessonListingModel);
        assertEquals(0, lessonListingModel.getLessons().size());
    }

    @Test
    void testEmptyLessons() {
        context.currentResource("/content/mccom/language-masters/en/lessons/jcr:content/root/lesson-listing");
        Lesson mockLesson = getLesson();
        Course mockCourse = getCourse(mockLesson);
        when(courseService.getCourse(anyString(), any(ResourceResolver.class))).thenReturn(mockCourse);
        when(courseService.getLessons(anyString(), any(ResourceResolver.class))).thenReturn(Collections.emptyList());
        lessonListingModel = context.request().adaptTo(LessonListingModel.class);
        assertNotNull(lessonListingModel);
        assertEquals(0, lessonListingModel.getLessons().size());
    }

    @Test
    void testNullLessons() {
        context.currentResource("/content/mccom/language-masters/en/lessons/jcr:content/root/lesson-listing");
        Lesson mockLesson = getLesson();
        Course mockCourse = getCourse(mockLesson);
        when(courseService.getCourse(anyString(), any(ResourceResolver.class))).thenReturn(mockCourse);
        when(courseService.getLessons(anyString(), any(ResourceResolver.class))).thenReturn(null);
        lessonListingModel = context.request().adaptTo(LessonListingModel.class);
        assertNotNull(lessonListingModel);
        assertEquals(0, lessonListingModel.getLessons().size());
    }

    @Test
    void testLessonListingSuccess() {
        context.currentResource("/content/mccom/language-masters/en/lessons/jcr:content/root/lesson-listing");

        Lesson mockLesson = getLesson();
        Course mockCourse = getCourse(mockLesson);

        when(courseService.getCourse(anyString(), any(ResourceResolver.class))).thenReturn(mockCourse);
        when(courseService.getLessons(anyString(), any(ResourceResolver.class))).thenReturn(List.of(mockLesson));
        when(msmLinkResolver.resolve(anyString(), any(Page.class), any(ResourceResolver.class))).thenReturn("/msm/link/link-1");

        lessonListingModel = context.request().adaptTo(LessonListingModel.class);
        assertNotNull(lessonListingModel);
        assertEquals(1, lessonListingModel.getLessons().size());

        Lesson lesson = lessonListingModel.getLessons().get(0);
        assertAll(
                () -> assertEquals("course-1", lessonListingModel.getCourseId()),
                () -> assertEquals("lesson-1", lesson.getId()),
                () -> assertEquals("Test Lesson", lesson.getTitle()),
                () -> assertEquals("test.png", lesson.getThumbnail()),
                () -> assertEquals("Read Article", lessonListingModel.getViewLessonLinkLabel()),
                () -> assertEquals("/msm/link/link-1", lesson.getPath())
        );
    }

    @Test
    void testLessonListingSuccess_lang_master() {
        context.currentResource("/content/mccom/language-masters/en/lessons/jcr:content/root/lesson-listing");

        Lesson mockLesson = getLesson();
        Course mockCourse = getCourse(mockLesson);

        when(courseService.getCourse(anyString(), any(ResourceResolver.class))).thenReturn(mockCourse);
        when(courseService.getLessons(anyString(), any(ResourceResolver.class))).thenReturn(List.of(mockLesson));
        when(msmLinkResolver.resolve(anyString(), any(Page.class), any(ResourceResolver.class))).thenReturn(null);

        lessonListingModel = context.request().adaptTo(LessonListingModel.class);
        assertNotNull(lessonListingModel);
        assertEquals(1, lessonListingModel.getLessons().size());

        Lesson lesson = lessonListingModel.getLessons().get(0);
        assertAll(
                () -> assertEquals("course-1", lessonListingModel.getCourseId()),
                () -> assertEquals("lesson-1", lesson.getId()),
                () -> assertEquals("Test Lesson", lesson.getTitle()),
                () -> assertEquals("test.png", lesson.getThumbnail()),
                () -> assertEquals("Read Article", lessonListingModel.getViewLessonLinkLabel()),
                () -> assertEquals("#", lesson.getPath())
        );
    }

    private static Lesson getLesson() {
        Lesson mockLesson = Lesson.builder()
                .id("lesson-1")
                .title("Test Lesson")
                .path("#")
                .thumbnail("test.png")
                .build();
        return mockLesson;
    }

    private  Course getCourse(Lesson mockLesson) {
       return Course.builder()
                .id("course-1")
                .title("Test Title")
                .path("")
                .thumbnail("test.png")
                .lessons(List.of(mockLesson))
                .lessonCount(1)
                .build();

    }
}
