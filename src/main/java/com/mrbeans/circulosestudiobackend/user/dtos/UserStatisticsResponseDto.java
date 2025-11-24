package com.mrbeans.circulosestudiobackend.user.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UserStatisticsResponseDto {

    @JsonProperty("totalActiveStudents")
    private Long totalActiveStudents;

    @JsonProperty("activeTutors")
    private List<TutorStatisticsDto> activeTutors;

    @JsonProperty("totalActiveTutors")
    private Long totalActiveTutors;

    @JsonProperty("totalActiveAdministrators")
    private Long totalActiveAdministrators;

    @JsonProperty("totalNewUsersThisMonth")
    private Long totalNewUsersThisMonth;

    @JsonProperty("filters")
    private FilterDto filters;
}
