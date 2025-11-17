package com.mrbeans.circulosestudiobackend.support_material.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SupportMaterialCreateDto {

    @NotBlank(message = "El título no debe estar vacío")
    private String title;

    @NotBlank(message = "La descripción no debe estar vacía")
    private String description;

    @NotBlank(message = "La categoría no debe estar vacía")
    private String category;

    @NotNull(message = "El id del grupo de trabajo no debe estar vacio")
    private UUID workgroupId;
}
