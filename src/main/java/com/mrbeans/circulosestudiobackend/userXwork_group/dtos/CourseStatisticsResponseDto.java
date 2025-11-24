package com.mrbeans.circulosestudiobackend.userXwork_group.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CourseStatisticsResponseDto {

    @JsonProperty("totalActiveCourses")
    private Long totalActiveCourses;

    @JsonProperty("courses")
    private List<CourseWithStatisticsDto> courses;
}
