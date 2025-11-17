package com.mrbeans.circulosestudiobackend.documents_x_support_material.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class FilesDto {

    private UUID id;

    private String originalFilename;

    private String url;

    @JsonProperty("tipo")
    private String contentType;

}
