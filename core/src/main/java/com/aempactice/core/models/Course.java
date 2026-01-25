package com.aempactice.core.models;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class Course {
    private String id;
    private String title;
    private String path;
    private String thumbnail;
    private int lessonCount;
    private List<Lesson> lessons;
}
