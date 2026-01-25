package com.aempactice.core.services.impl;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.aempactice.core.models.Course;
import com.aempactice.core.models.Lesson;
import com.aempactice.core.services.CourseService;

import com.aempactice.core.services.MsmLinkResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component(service = CourseService.class, immediate = true)
public class CourseServiceImpl implements CourseService {

    protected static final String COURSE_MODEL_PATH = "/conf/aempractice/settings/dam/cfm/models/course";

    @OSGiService
    private MsmLinkResolver msmLinkResolver;

    @Override
    public Course getCourse(String coursePath, ResourceResolver resolver) {
        if (resolver == null || StringUtils.isBlank(coursePath)) {
            log.warn("Invalid parameters: resolver={}, coursePath={}", resolver, coursePath);
            return null;
        }

        Resource resource = resolver.getResource(coursePath);
        if (resource == null) {
            log.warn("Course resource not found at path: {}", coursePath);
            return null;
        }

        ContentFragment courseFragment = resource.adaptTo(ContentFragment.class);
        if (courseFragment == null) {
            log.warn("Resource at {} is not a Content Fragment", coursePath);
            return null;
        }

        return buildCourse(courseFragment, resolver);
    }

    @Override
    public boolean isCourseFragment(ContentFragment fragment) {

        if (fragment == null) {
            return false;
        }

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

    @Override
    public List<Lesson> getLessons(String coursePath, ResourceResolver resolver) {
        if (resolver == null || StringUtils.isBlank(coursePath)) {
            log.warn("Invalid parameters for getLessons");
            return List.of();
        }

        Resource resource = resolver.getResource(coursePath);
        if (resource == null) {
            return List.of();
        }

        ContentFragment courseFragment = resource.adaptTo(ContentFragment.class);
        if (courseFragment == null) {
            return List.of();
        }

        return extractLessons(courseFragment, resolver);
    }

    @Override
    public Lesson getLesson(String coursePath, String lessonId, ResourceResolver resolver) {
        List<Lesson> lessons = getLessons(coursePath, resolver);
        return lessons.stream()
                .filter(lesson -> lesson.getId().equals(lessonId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int getLessonCount(String coursePath, ResourceResolver resolver) {
        return getLessons(coursePath, resolver).size();
    }

    /**
     * Builds Course object from Content Fragment
     */
    private Course buildCourse(ContentFragment courseFragment, ResourceResolver resolver) {
        String id = getFragmentValue(courseFragment, "courseId");
        String title = getFragmentValue(courseFragment, "courseTitle");
        String path = getFragmentValue(courseFragment, "courseLink");
        String thumbnail = getFragmentValue(courseFragment, "courseThumbnail");

        List<Lesson> lessons = extractLessons(courseFragment, resolver);

        if (StringUtils.isAnyBlank(id, title, path)) {
            log.warn("Invalid course fragment: {} (missing mandatory fields)", courseFragment.getName());
            return null;
        }

        return Course.builder()
                .id(id)
                .title(title)
                .path(path)
                .thumbnail(thumbnail)
                .lessonCount(lessons.size())
                .lessons(lessons)
                .build();
    }

    /**
     * Extracts lessons from course fragment's lessons multifield.
     * Supports both "lessonId" and "chapterId" field names for backwards compatibility.
     */
    private List<Lesson> extractLessons(ContentFragment courseFragment, ResourceResolver resolver) {
        if (!courseFragment.hasElement("lessons")) {
            log.debug("No lessons element found in course fragment: {}", courseFragment.getName());
            return List.of();
        }

        List<String> lessonPaths = getFragmentValueList(courseFragment, "lessons");
        if (lessonPaths.isEmpty()) {
            return List.of();
        }

        List<Lesson> lessons = new ArrayList<>();
        lessonPaths.forEach(lessonPath -> {

            Resource lessonResource = resolver.getResource(lessonPath);
            if (lessonResource == null) {
                log.warn("Lesson resource not found: {}", lessonPath);
                return;
            }

            ContentFragment lessonFragment = lessonResource.adaptTo(ContentFragment.class);
            if (lessonFragment == null) {
                log.warn("Resource at {} is not a Content Fragment", lessonPath);
                return;
            }

            Lesson lesson = buildLesson(lessonFragment);
            if (lesson != null) {
                lessons.add(lesson);
            }
        });
        log.debug("Extracted {} lessons from course fragment: {}", lessons.size(), courseFragment.getName());
        return lessons;
    }

    /**
     * Builds Lesson object from lesson Content Fragment.
     * Return chapterId field names for backwards compatibility.
     */
    private Lesson buildLesson(ContentFragment lessonFragment) {

        String id = getFragmentValue(lessonFragment, "chapterId");
        String title = getFragmentValue(lessonFragment, "chapterTitle");
        String link = getFragmentValue(lessonFragment, "chapterLink");
        String thumbnail = getFragmentValue(lessonFragment, "chapterThumbnail");

        if (StringUtils.isAnyBlank(id, title, link)) {
            log.warn("Invalid lesson fragment: {} (missing mandatory fields)", lessonFragment.getName());
            return null;
        }

        return Lesson.builder()
                .id(id)
                .title(title)
                .path(link)
                .thumbnail(thumbnail)
                .build();
    }

    /**
     * Safely reads Content Fragment element value.
     * Extracted from LessonJourneyHelper for service layer.
     */
    private String getFragmentValue(ContentFragment fragment, String elementName) {
        if (fragment == null || !fragment.hasElement(elementName)) {
            return StringUtils.EMPTY;
        }

        return Optional.ofNullable(fragment.getElement(elementName).getContent())
                .orElse(StringUtils.EMPTY);
    }

    private List<String> getFragmentValueList(ContentFragment fragment, String elementName) {
        if (fragment == null || !fragment.hasElement(elementName)) {
            return List.of();
        }

        String arrStringValue = Optional.ofNullable(fragment.getElement(elementName).getContent())
                .orElse(StringUtils.EMPTY);

        return List.of(arrStringValue.split("\n"));
    }
}
