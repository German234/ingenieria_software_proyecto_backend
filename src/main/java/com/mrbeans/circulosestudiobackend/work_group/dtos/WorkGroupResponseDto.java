package com.mrbeans.circulosestudiobackend.work_group.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class WorkGroupResponseDto {

    @JsonProperty("_id")
    private UUID id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("backgroundImage")
    private String backgroundImage;

    @JsonProperty("slug")
    private String slug;
}
