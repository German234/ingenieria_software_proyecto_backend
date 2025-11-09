package com.mrbeans.circulosestudiobackend.documents_x_support_material.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateDocumentXSupportMaterialDto {

    @JsonProperty("titulo")
    private String title;

    private String descripcion;

    private String categoria;

    private List<UUID> documentIds;
}

