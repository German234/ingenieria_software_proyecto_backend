package com.mrbeans.circulosestudiobackend.work_group.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class WorkGroupCreateDto {

    @NotBlank(message = "El nombre no debe estar vacío")
    private String name;

    @NotBlank(message = "La imagen de fondo no debe estar vacía")
    private UUID backgroundDocumentId;
    
}
