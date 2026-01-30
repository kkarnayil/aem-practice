package com.aempactice.core.services;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.aempactice.core.models.Course;
import com.aempactice.core.models.Lesson;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

/**
 * Service for reading Course and Lesson data from Content Fragments.
 * Centralizes all CF reading logic for consistent behavior across components.
 */
public interface CourseService {

    /**
     * Reads a Course Content Fragment and returns structured data.
     */
    Course getCourse(String coursePath, ResourceResolver resolver);

    /**
     * Reads all lessons from a course fragment.
     */
    List<Lesson> getLessons(String coursePath, ResourceResolver resolver);

    /**
     * Reads a single lesson by ID.
     */
    Lesson getLesson(String coursePath, String lessonId, ResourceResolver resolver);

    /**
     * Counts lessons in a course (for progress tracking).
     */
    int getLessonCount(String coursePath, ResourceResolver resolver);
}
