package com.mrbeans.circulosestudiobackend.documents.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDocumentDto {
    private String category;
    private String originalFilename;
}
