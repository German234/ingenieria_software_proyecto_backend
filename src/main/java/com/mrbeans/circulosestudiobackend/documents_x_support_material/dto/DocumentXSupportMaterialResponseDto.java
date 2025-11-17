package com.mrbeans.circulosestudiobackend.documents_x_support_material.dto;

import com.mrbeans.circulosestudiobackend.documents.dtos.ResponseDocumentDto;
import com.mrbeans.circulosestudiobackend.support_material.dtos.SupportMaterialResponseDto;
import lombok.Data;

@Data
public class DocumentXSupportMaterialResponseDto {
    
    private ResponseDocumentDto document;
    private SupportMaterialResponseDto supportMaterial;
} 