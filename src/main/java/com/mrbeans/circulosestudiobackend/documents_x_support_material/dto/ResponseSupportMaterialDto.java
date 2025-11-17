package com.mrbeans.circulosestudiobackend.documents_x_support_material.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ResponseSupportMaterialDto {
    @JsonProperty("_id")
    private UUID supportMaterialId;

    @JsonProperty("titulo")
    private String titulo;

    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("categoria")
    private String categoria;

    @JsonProperty("documentos")
    private List<FilesDto> files;
    
    @JsonProperty("numeroComentarios")
    private Long commentCount;
}

