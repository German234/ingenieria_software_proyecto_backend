package com.mrbeans.circulosestudiobackend.documents_x_support_material.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DocumentXSupportMaterialRequestDto {
    
    @NotNull(message = "El ID del documento no puede estar vacío")
    private UUID documentId;
    
    @NotNull(message = "El ID del material de apoyo no puede estar vacío")
    private UUID supportMaterialId;
} 