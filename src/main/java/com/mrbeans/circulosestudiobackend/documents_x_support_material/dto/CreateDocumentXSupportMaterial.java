package com.mrbeans.circulosestudiobackend.documents_x_support_material.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateDocumentXSupportMaterial {

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotBlank(message = "La categoría no puede estar vacía")
    private String categoria;

    @NotNull(message = "El ID del grupo de trabajo no puede estar vacío")
    private UUID workgroupId;

    @NotEmpty(message = "La lista de IDs de documentos no puede estar vacía")
    private List<UUID> documentIds;
}
