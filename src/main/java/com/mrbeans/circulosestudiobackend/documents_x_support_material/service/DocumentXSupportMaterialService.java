package com.mrbeans.circulosestudiobackend.documents_x_support_material.service;

import com.mrbeans.circulosestudiobackend.documents_x_support_material.dto.*;

import java.util.List;
import java.util.UUID;

public interface DocumentXSupportMaterialService {

    void createSupportMaterialXDocument(String UserName, CreateDocumentXSupportMaterial dto);

    void assignDocumentsToSupportMaterial(AssignDocumentsDto dto);

    void deleteDocumentFromSupportMaterial(UUID supportMaterialId, UUID documentId);

    void updateSupportMaterial(UUID supportMaterialId, UpdateDocumentXSupportMaterialDto dto);

    List<ResponseSupportMaterialDto> getSupportMaterialsByWorkgroupId(UUID workGroupId);

} 