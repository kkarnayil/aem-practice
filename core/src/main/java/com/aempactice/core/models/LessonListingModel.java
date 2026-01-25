package com.aempactice.core.models;

import com.aempactice.core.services.CourseService;
import com.aempactice.core.services.MsmLinkResolver;
import com.day.cq.wcm.api.Page;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Model(adaptables = SlingHttpServletRequest.class, resourceType = LessonListingModel.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LessonListingModel {

    public static final String RESOURCE_TYPE = "aempractice/components/lessonlisting";

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private CourseService courseService;

    @OSGiService
    private MsmLinkResolver msmLinkResolver;

    @Getter
    @ValueMapValue
    private String coursePath;

    @Getter
    @Default(values = "Read Article")
    @ValueMapValue
    private String viewLessonLinkLabel;

    @ScriptVariable
    private Page currentPage;

    @Getter
    private List<Lesson> lessons;

    @Getter
    private String courseId;

    @PostConstruct
    void init() {

        if (resourceResolver == null || StringUtils.isBlank(coursePath)) {
            log.warn("ResourceResolver or coursePath is missing");
            lessons = List.of();
            return;
        }

        Course course = courseService.getCourse(coursePath, resourceResolver);

        if (course == null) {
            log.warn("No Course found at path: {}", coursePath);
            this.lessons = List.of();
            return;
        }

        this.courseId = course.getId();
        List<Lesson> courseLessons = courseService.getLessons(coursePath, resourceResolver);

        if (courseLessons == null || courseLessons.isEmpty()) {
            log.warn("No lessons found for Course at path: {}", coursePath);
            this.lessons = List.of();
            return;
        }

        this.lessons = courseLessons.stream()
                .map(this::enrichLessonWithMsmLink)
                .sorted(Comparator.comparing(Lesson::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());


        log.info("Collected {} lessons from Course: {}", lessons.size(), coursePath);
    }

    /**
     * Enriches a Lesson with its MSM link based on the current page context.
     *
     * @param lesson the Lesson to enrich
     * @return the enriched Lesson
     */
    private Lesson enrichLessonWithMsmLink(Lesson lesson) {
        String msmLink = msmLinkResolver.resolve(
                lesson.getPath(), currentPage, resourceResolver);

        return Lesson.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .path(msmLink != null ? msmLink : lesson.getPath())
                .thumbnail(lesson.getThumbnail())
                .build();
    }
}
