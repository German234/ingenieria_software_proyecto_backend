package com.mrbeans.circulosestudiobackend.comments.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCommentDto {

    @NotBlank(message = "El mensaje no debe estar vacío")
    private String message;
}