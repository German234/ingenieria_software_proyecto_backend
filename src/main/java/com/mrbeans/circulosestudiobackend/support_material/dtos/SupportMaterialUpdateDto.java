package com.mrbeans.circulosestudiobackend.support_material.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class SupportMaterialUpdateDto {

    private String title;

    private String description;

    private String category;

    private UUID workgroupId;
}