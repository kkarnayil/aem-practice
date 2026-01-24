package com.aempactice.core.models;

import com.aempactice.core.beans.Lesson;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(AemContextExtension.class)
public class LessonListingModelTest {

    LessonListingModel lessonListingModel;

    private final AemContext context = new AemContextBuilder()
            .build();

    @BeforeEach
    public void setUp() {
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
    void testLessonListingSuccess() {
        context.currentResource("/content/mccom/language-masters/en/lessons/jcr:content/root/lesson-listing");
        lessonListingModel = context.request().adaptTo(LessonListingModel.class);
        assertNotNull(lessonListingModel);
        assertEquals(1, lessonListingModel.getLessons().size());

        Lesson course = lessonListingModel.getLessons().get(0);
        assertAll(
                () -> assertEquals("course-1", lessonListingModel.getCourseId()),
                () -> assertEquals("lesson-1", course.getId()),
                () -> assertEquals("Introduction to Lesson 1", course.getTitle()),
                () -> assertEquals("", course.getThumbnail()),
                () -> assertEquals("Read Article", lessonListingModel.getViewLessonLinkLabel())
        );
    }
}
