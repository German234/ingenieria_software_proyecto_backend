package com.mrbeans.circulosestudiobackend.documents.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ResponseDocumentDto {
    private UUID id;
    private String category;
    private String originalFilename;
    private String url;
}
