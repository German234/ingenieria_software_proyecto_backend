package com.mrbeans.circulosestudiobackend.userXwork_group.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mrbeans.circulosestudiobackend.documents_x_support_material.dto.ResponseSupportMaterialDto;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ResponseWorkGroupDto {
    @JsonProperty("_id")
    private UUID id;

    @JsonProperty("nombre")
    private String name;

    @JsonProperty("backgroundImage")
    private String backgroundImage;

    @JsonProperty("alumnos")
    List<UserDto> alumnos;

    @JsonProperty("tutores")
    List<UserDto> tutors;

    @JsonProperty("publicaciones")
    List<ResponseSupportMaterialDto> files;
}
