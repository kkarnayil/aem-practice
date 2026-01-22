package com.aempactice.core.models;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.aempactice.core.beans.Course;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.aempactice.core.utils.CommonUtils.getFragmentValue;
import static com.aempactice.core.utils.CommonUtils.getMsmLink;

@Slf4j
@Model(adaptables = SlingHttpServletRequest.class, resourceType = CourseListingModel.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CourseListingModel {

    public static final String RESOURCE_TYPE = "aempractice/components/courselisting";

    @SlingObject
    private ResourceResolver resourceResolver;

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

    @OSGiService
    private LiveRelationshipManager liveRelationshipManager;

    @ScriptVariable
    private Page currentPage;

    @Getter
    private List<Course> courses;

    @PostConstruct
    void init() {

        if (resourceResolver == null || StringUtils.isBlank(courseFolderPath)) {
            log.warn("ResourceResolver or courseFolderPath is missing");
            courses = List.of();
            return;
        }

        Resource courseFolder = resourceResolver.getResource(courseFolderPath);
        if (courseFolder == null || !courseFolder.hasChildren()) {
            log.warn("Invalid or empty course folder: {}", courseFolderPath);
            courses = List.of();
            return;
        }

        courses = collectCourses(courseFolder);
        log.info("Collected {} courses from folder: {}", courses.size(), courseFolderPath);
    }

    /**
     * Collects courses from Content Fragments under the given folder
     */
    private List<Course> collectCourses(Resource courseFolder) {

        return StreamSupport.stream(courseFolder.getChildren().spliterator(), false)
                .map(child -> child.adaptTo(ContentFragment.class))
                .filter(Objects::nonNull)
                .map(this::buildCourse)
                .flatMap(Optional::stream)
                .sorted(Comparator.comparing(Course::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    /**
     * Builds Course object from Content Fragment
     */
    private Optional<Course> buildCourse(ContentFragment fragment) {

        String id = getFragmentValue(fragment, "courseId");
        String title = getFragmentValue(fragment, "courseTitle");
        String path = getMsmLink(getFragmentValue(fragment, "courseLink"), resourceResolver, liveRelationshipManager, currentPage);
        String thumbnail = getFragmentValue(fragment, "courseThumbnail");

        if (StringUtils.isAnyBlank(id, path, title)) {
            log.warn("Invalid course fragment: {}", fragment.getTitle());
            return Optional.empty();
        }

        return Optional.of(Course.builder()
                .id(id)
                .title(title)
                .path(path)
                .thumbnail(thumbnail)
                .build());
    }
}
