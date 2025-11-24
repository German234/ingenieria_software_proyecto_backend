package com.mrbeans.circulosestudiobackend.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

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

    @JsonProperty("filters")
    private FilterDto filters;
}
