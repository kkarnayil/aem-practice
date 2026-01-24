package com.aempactice.core.models;

import com.aempactice.core.beans.Course;
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
public class CourseListingModelTest {

    CourseListingModel courseListingModel;

    private final AemContext context = new AemContextBuilder()
            .build();

    @BeforeEach
    public void setUp() {
        context.load().json("/json/course-lessons-journey.json", "/content");
    }

    @Test
    void testCourseListingUnconfigured() {
        context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-listing-unconfigured");
        courseListingModel = context.request().adaptTo(CourseListingModel.class);
        assertNotNull(courseListingModel);
        assertEquals(0, courseListingModel.getCourses().size());
    }

    @Test
    void testCourseListingInvalidPath() {
        context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-listing-wrong-path");
        courseListingModel = context.request().adaptTo(CourseListingModel.class);
        assertNotNull(courseListingModel);
        assertEquals(0, courseListingModel.getCourses().size());
    }

    @Test
    void testCoursesSuccess() {
        context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-listing");
        courseListingModel = context.request().adaptTo(CourseListingModel.class);
        assertNotNull(courseListingModel);
        assertEquals(1, courseListingModel.getCourses().size());

        Course course = courseListingModel.getCourses().get(0);
        assertAll(
                () -> assertEquals("course-1", course.getId()),
                () -> assertEquals("Introduction to AEM", course.getTitle()),
                () -> assertEquals("", course.getThumbnail()),
                () -> assertEquals(2, course.getLessonsCount()),
                () -> assertEquals("View Course", courseListingModel.getViewCourseLinkLabel())
        );
    }
}
