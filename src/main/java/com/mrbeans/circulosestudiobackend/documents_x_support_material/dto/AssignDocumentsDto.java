package com.mrbeans.circulosestudiobackend.documents_x_support_material.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AssignDocumentsDto {

    private UUID supportMaterialId;

    private List<UUID> documentIds;

}

