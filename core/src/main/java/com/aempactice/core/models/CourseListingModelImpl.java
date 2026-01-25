package com.aempactice.core.models;

import com.day.cq.wcm.api.Page;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.aempactice.core.services.CourseService;
import com.aempactice.core.services.MsmLinkResolver;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CourseListingModel using service architecture.
 * Validates that CourseService and NavigationService work correctly
 * before building more components on top.
 */
@Slf4j
@Model(adaptables = SlingHttpServletRequest.class,
        resourceType = CourseListingModelImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CourseListingModelImpl {

    public static final String RESOURCE_TYPE = "aempractice/components/courselisting";

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private CourseService courseService;

    @OSGiService
    private MsmLinkResolver msmLinkResolver;

    @ScriptVariable
    private Page currentPage;

    @Getter
    @ValueMapValue
    private String heading;

    @Getter
    @ValueMapValue
    private String courseFolderPath;

    @Getter
    @Default(values = "View Course")
    @ValueMapValue
    private String viewCourseLinkLabel;

    @Getter
    private List<Course> courses;

    @PostConstruct
    void init() {
        if (StringUtils.isBlank(courseFolderPath)) {
            log.warn("courseFolderPath is not configured");
            courses = List.of();
            return;
        }

        Resource courseFolder = resourceResolver.getResource(courseFolderPath);
        if (courseFolder == null || !courseFolder.hasChildren()) {
            log.warn("Invalid course folder: {}", courseFolderPath);
            courses = List.of();
            return;
        }

        List<Course> result = new ArrayList<>();
        collectCourses(courseFolder, result);

        courses = result.stream()
                .sorted(Comparator.comparing(Course::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        log.info("Collected {} courses from folder: {}", courses.size(), courseFolderPath);
    }

    /**
     * Recursively collects Course fragments from the resource tree.
     *
     * @param resource Resource to start from
     * @param courses  List to populate with found courses
     */
    private void collectCourses(Resource resource, List<Course> courses) {

        if (resource == null) {
            return;
        }

        ContentFragment fragment = resource.adaptTo(ContentFragment.class);
        if (fragment != null && courseService.isCourseFragment(fragment)) {
            Course course = courseService.getCourse(resource.getPath(), resourceResolver);
            if (course != null) {
                courses.add(enrichCourseWithMsmLink(course));
            }
        } else if (resource.hasChildren()) {
            for (Resource child : resource.getChildren()) {
                collectCourses(child, courses);
            }
        }
    }

    private Course enrichCourseWithMsmLink(Course course) {
        String msmLink = msmLinkResolver.resolve(
                course.getPath(), currentPage, resourceResolver);

        return Course.builder()
                .id(course.getId())
                .title(course.getTitle())
                .path(msmLink != null ? msmLink : course.getPath())
                .thumbnail(course.getThumbnail())
                .lessonCount(course.getLessonCount())
                .lessons(course.getLessons())
                .build();
    }
}
