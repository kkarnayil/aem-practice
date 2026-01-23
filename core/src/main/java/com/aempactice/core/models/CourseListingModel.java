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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.aempactice.core.utils.CommonUtils.getFragmentValue;
import static com.aempactice.core.utils.CommonUtils.getMsmLink;

@Slf4j
@Model(adaptables = SlingHttpServletRequest.class, resourceType = CourseListingModel.RESOURCE_TYPE, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CourseListingModel {

    public static final String RESOURCE_TYPE = "aempractice/components/courselisting";
    private static final String COURSE_MODEL_PATH = "/conf/aempractice/settings/dam/cfm/models/course";

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

        List<Course> result = new ArrayList<>();
        collectCoursesRecursive(courseFolder, result);

        courses = result.stream()
                .sorted(Comparator.comparing(Course::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        log.info("Collected {} courses from folder: {}", courses.size(), courseFolderPath);
    }


    private void collectCoursesRecursive(Resource resource, List<Course> courses) {

        if (resource == null) {
            return;
        }

        ContentFragment fragment = resource.adaptTo(ContentFragment.class);
        if (fragment != null && isCourseFragment(fragment)) {
            buildCourse(fragment).ifPresent(courses::add);
        } else if(resource.hasChildren()) {
            for (Resource child : resource.getChildren()) {
                collectCoursesRecursive(child, courses);
            }
        }


    }

    private boolean isCourseFragment(ContentFragment fragment) {

        Resource fragmentResource = fragment.adaptTo(Resource.class);
        if (fragmentResource == null) {
            return false;
        }

        Resource dataResource = fragmentResource.getChild("jcr:content/data");
        if (dataResource == null) {
            return false;
        }

        String modelPath = dataResource.getValueMap().get("cq:model", String.class);
        return StringUtils.equals(modelPath, COURSE_MODEL_PATH);
    }

    /**
     * Builds Course object from Content Fragment
     */
    private Optional<Course> buildCourse(ContentFragment courseFragment) {

        String id = getFragmentValue(courseFragment, "courseId");
        String title = getFragmentValue(courseFragment, "courseTitle");
        String path = getMsmLink(getFragmentValue(courseFragment, "courseLink"), resourceResolver, liveRelationshipManager, currentPage);
        String thumbnail = getFragmentValue(courseFragment, "courseThumbnail");
        int lessonsCount = getLessonCount(courseFragment);

        if (StringUtils.isAnyBlank(id, path, title)) {
            log.warn("Invalid course fragment: {}", courseFragment.getTitle());
            return Optional.empty();
        }

        return Optional.of(Course.builder()
                .id(id)
                .title(title)
                .path(path)
                .thumbnail(thumbnail)
                .lessonsCount(lessonsCount)
                .build());
    }

    private int getLessonCount(ContentFragment courseFragment) {
        int lessonsCount = 0;
        if (courseFragment.hasElement("lessons")) {
            lessonsCount = Optional.ofNullable(courseFragment.getElement("lessons").getValue().getValue(String[].class))
                    .map(lessonsArr -> lessonsArr.length)
                    .orElse(0);
        }
        return lessonsCount;
    }
}
