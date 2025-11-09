package com.mrbeans.circulosestudiobackend.documents_x_support_material.repository;

import com.mrbeans.circulosestudiobackend.documents_x_support_material.dto.ResponseSupportMaterialDto;
import com.mrbeans.circulosestudiobackend.documents_x_support_material.entity.DocumentXSupportMaterialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IDocumentXSupportMaterialRepository extends JpaRepository<DocumentXSupportMaterialEntity, UUID> {
    
    boolean existsByDocumentIdAndSupportMaterialId(UUID documentId, UUID supportMaterialId);

    List<DocumentXSupportMaterialEntity> findAllBySupportMaterialId(UUID smId);
    void deleteByDocumentIdAndSupportMaterialId(UUID documentId, UUID supportMaterialId);

} 