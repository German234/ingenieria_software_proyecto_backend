package com.mrbeans.circulosestudiobackend.documents.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDocumentDto {

    @NotBlank(message = "Category no puede estar vacío")
    private String category;

    @NotBlank(message = "OriginalFilename no puede estar vacío")
    private String originalFilename;

}
