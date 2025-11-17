package com.mrbeans.circulosestudiobackend.comments.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateCommentDto {

    @NotBlank(message = "El mensaje no debe estar vacío")
    private String message;

    @NotNull(message = "El ID del material de soporte no debe estar vacío")
    private UUID supportMaterialId;
}
