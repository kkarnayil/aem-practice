package com.aempactice.core.models;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.aempactice.core.beans.Lesson;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.aempactice.core.utils.CommonUtils.getFragmentValue;
import static com.aempactice.core.utils.CommonUtils.getFragmentValueList;

@Slf4j
@Model(adaptables = SlingHttpServletRequest.class, resourceType = LessonListingModel.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LessonListingModel {

    public static final String RESOURCE_TYPE = "aempractice/components/lessonlisting";

    @SlingObject
    private ResourceResolver resourceResolver;

    @Getter
    @ValueMapValue
    private String coursePath;

    @Getter
    @Default(values = "Read Article")
    @ValueMapValue
    private String viewLessonLinkLabel;

    @OSGiService
    private LiveRelationshipManager liveRelationshipManager;

    @ScriptVariable
    private Page currentPage;

    @Getter
    private List<Lesson> lessons;

    @Getter
    private String courseId;

    @PostConstruct
    void init() {

        if (StringUtils.isBlank(coursePath)) {
            log.warn("coursePath is missing");
            lessons = List.of();
            return;
        }

        List<String> lessonPaths = new ArrayList<>();
        Optional.ofNullable(resourceResolver.getResource(coursePath))
                .map(res -> res.adaptTo(ContentFragment.class))
                .ifPresentOrElse(courseCf -> {
                    // Read courseId from course fragment
                    this.courseId = getFragmentValue(courseCf, "courseId");
                    lessonPaths.addAll(getFragmentValueList(courseCf, "lessons"));
                }, () -> log.warn("Course fragment not found or not a Content Fragment: {}", coursePath));

        log.info("Collected {} lessons from Course: {}", lessonPaths.size(), coursePath);
        if (!lessonPaths.isEmpty()) {
            collectLessons(lessonPaths);
        }
    }

    private void collectLessons(List<String> lessonPaths) {
        lessons = lessonPaths.stream()
                .map(path -> resourceResolver.getResource(path))
                .filter(Objects::nonNull)
                .map(res -> res.adaptTo(ContentFragment.class))
                .filter(Objects::nonNull)
                .map(this::buildLesson)
                .flatMap(Optional::stream)
                .sorted(Comparator.comparing(Lesson::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    private Optional<Lesson> buildLesson(ContentFragment lessonFragment) {

        String id = getFragmentValue(lessonFragment, "lessonId");
        String title = getFragmentValue(lessonFragment, "lessonTitle");
        String path = getFragmentValue(lessonFragment, "lessonLink");
        String thumbnail = getFragmentValue(lessonFragment, "lessonThumbnail");

        if (StringUtils.isAnyBlank(id, title, path)) {
            log.warn("Invalid lesson fragment at {} (missing mandatory fields)",
                    lessonFragment.getName());
            return Optional.empty();
        }

        return Optional.of(Lesson.builder()
                .id(id)
                .title(title)
                .path(path)
                .thumbnail(thumbnail)
                .build());
    }
}
