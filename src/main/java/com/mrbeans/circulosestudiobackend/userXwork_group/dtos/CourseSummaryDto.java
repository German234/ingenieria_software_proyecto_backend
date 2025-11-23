package com.mrbeans.circulosestudiobackend.userXwork_group.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class CourseSummaryDto {
    @JsonProperty("_id")
    private UUID id;

    @JsonProperty("nombre")
    private String name;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("backgroundImage")
    private String backgroundImage;

    @JsonProperty("cantidadAlumnos")
    private Integer cantidadAlumnos;

    @JsonProperty("cantidadTutores")
    private Integer cantidadTutores;
}