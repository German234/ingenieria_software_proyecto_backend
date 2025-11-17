package com.mrbeans.circulosestudiobackend.support_material.service;

import com.mrbeans.circulosestudiobackend.support_material.dtos.SupportMaterialCreateDto;
import com.mrbeans.circulosestudiobackend.support_material.dtos.SupportMaterialResponseDto;
import com.mrbeans.circulosestudiobackend.support_material.dtos.SupportMaterialUpdateDto;
import com.mrbeans.circulosestudiobackend.support_material.entity.SupportMaterialEntity;

import java.util.List;
import java.util.UUID;

public interface SupportMaterialService {

    SupportMaterialEntity createSupportMaterial(SupportMaterialCreateDto supportMaterialCreateDto);
    SupportMaterialResponseDto updateSupportMaterial(UUID id, SupportMaterialUpdateDto supportMaterialUpdateDto);
    SupportMaterialResponseDto findById(UUID id);
    List<SupportMaterialResponseDto> findAllSupportMaterials();
    List<SupportMaterialResponseDto> findByCategory(String category);
    List<SupportMaterialResponseDto> findByWorkGroupId(UUID workgroupId);
    void deleteSupportMaterial(UUID id);
}

