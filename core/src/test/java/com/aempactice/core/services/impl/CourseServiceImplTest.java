package com.aempactice.core.services.impl;

import com.aempactice.core.models.Course;
import com.aempactice.core.models.Lesson;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class CourseServiceImplTest {

    @InjectMocks
    CourseServiceImpl courseService;

    private final AemContext context = new AemContextBuilder().build();

    @BeforeEach
    void setup() {
        context.load().json("/json/course-lessons-journey.json", "/content");
    }

    @Test
    void getCourseEmptyCoursePath() {
        Resource resource = context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-unconfigured");
        assertNull(courseService.getCourse(null, resource.getResourceResolver()));
    }

    @Test
    void getCourseNullResolver() {
        Resource resource = context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-unconfigured");
        assertNull(courseService.getCourse(null, null));
    }

    @Test
    void getCoursePathNotResource() {
        Resource resource = context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-unconfigured");
        assertNull(courseService.getCourse("/content/mccom/language-masters/en/courses/non-existent", resource.getResourceResolver()));
    }

    @Test
    void getCoursePathNotContentFragment() {
        Resource resource = context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-unconfigured");
        assertNull(courseService.getCourse(resource.getPath(), resource.getResourceResolver()));
    }

    @Test
    void getValidCourse() {
        Resource resource = context.currentResource("/content/dam/courses/course-1/course-1");
        Course course = courseService.getCourse(resource.getPath(), resource.getResourceResolver());
        assertAll(
                () -> assertNotNull(course),
                () -> assertEquals("course-1", course.getId()),
                () -> assertEquals(1, course.getLessons().size()),
                () -> assertEquals("lesson-1", course.getLessons().get(0).getId())
        );
    }

    @Test
    void getInvalidCourse() {
        Course course = courseService.getCourse("/invalid-path", context.request().getResourceResolver());
        assertNull(course);
    }

    @Test
    void getCourseWithNoLessons() {
        Resource resource = context.currentResource("/content/dam/courses/course-4-with-no-lessons");
        Course course = courseService.getCourse(resource.getPath(), resource.getResourceResolver());
        assertNotNull(course);
        assertEquals(0, course.getLessons().size());
    }

    @Test
    void getCourseWithEmptyLessons() {
        Resource resource = context.currentResource("/content/dam/courses/course-5-with-empty-lessons");
        Course course = courseService.getCourse(resource.getPath(), resource.getResourceResolver());
        assertNotNull(course);
        assertEquals(0, course.getLessons().size());
    }

    @Test
    void getLessonEmptyCoursePath() {
        Resource resource = context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-unconfigured");
        assertEquals(0, courseService.getLessons(null, resource.getResourceResolver()).size());
    }

    @Test
    void getLessonNullResolver() {
        assertEquals(0, courseService.getLessons(null, null).size());
    }

    @Test
    void getLessonPathNotResource() {
        Resource resource = context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-unconfigured");
        assertEquals(0, courseService.getLessons("/content/mccom/language-masters/en/courses/non-existent", resource.getResourceResolver()).size());
    }

    @Test
    void getLessonPathNotContentFragment() {
        Resource resource = context.currentResource("/content/mccom/language-masters/en/courses/jcr:content/root/course-unconfigured");
        assertEquals(0, courseService.getLessons(resource.getPath(), resource.getResourceResolver()).size());
    }

    @Test
    void getValidLesson() {
        Resource resource = context.currentResource("/content/dam/courses/course-1/course-1");
        Lesson lesson = courseService.getLessons(resource.getPath(), resource.getResourceResolver()).get(0);
        assertAll(
                () -> assertNotNull(lesson),
                () -> assertEquals("lesson-1", lesson.getId())
        );
    }

    @Test
    void getLessonCount() {
        Resource resource = context.currentResource("/content/dam/courses/course-1/course-1");
        int count = courseService.getLessonCount(resource.getPath(), resource.getResourceResolver());
        assertEquals(1, count);
    }

    @Test
    void getLesson(){
        Resource resource = context.currentResource("/content/dam/courses/course-1/course-1");
        Lesson lesson = courseService.getLesson(resource.getPath(), "lesson-1", resource.getResourceResolver());
        assertAll(
                () -> assertNotNull(lesson),
                () -> assertEquals("lesson-1", lesson.getId())
        );
    }
}
