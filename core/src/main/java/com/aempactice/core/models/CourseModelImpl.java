package com.aempactice.core.models;

import com.aempactice.core.services.CourseService;
import com.aempactice.core.services.MsmLinkResolver;
import com.day.cq.wcm.api.Page;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;

/**
 * CourseListingModel using service architecture.
 * Validates that CourseService and NavigationService work correctly
 * before building more components on top.
 */
@Model(adaptables = SlingHttpServletRequest.class,
        resourceType = CourseModelImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CourseModelImpl {

    public static final String RESOURCE_TYPE = "aempractice/components/courselisting";

    private static final Logger log = LoggerFactory.getLogger(CourseModelImpl.class);

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
    private String coursePath;

    @Getter
    @Default(values = "View Course")
    @ValueMapValue
    private String viewCourseLinkLabel;

    @Getter
    private Course course;

    @PostConstruct
    void init() {

        if (StringUtils.isBlank(coursePath)) {
            log.warn("courseFolderPath is not configured");
            return;
        }

        Optional.ofNullable(courseService.getCourse(coursePath, resourceResolver))
                .ifPresentOrElse(
                        course -> this.course = enrichCourseWithMsmLink(course),
                        () -> log.warn("No course found at path: {}", coursePath)
                );
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
