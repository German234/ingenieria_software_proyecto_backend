package com.mrbeans.circulosestudiobackend.user.dtos;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TutorStatisticsDto {

    @JsonProperty("_id")
    private UUID id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("email")
    private String email;

    @JsonProperty("image")
    private String imageUrl;

    @JsonProperty("assignedStudentsCount")
    private Long assignedStudentsCount;

    @JsonProperty("lastActivityDate")
    private String lastActivityDate;
}
