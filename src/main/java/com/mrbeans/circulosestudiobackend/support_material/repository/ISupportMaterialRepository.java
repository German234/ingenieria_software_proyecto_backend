package com.mrbeans.circulosestudiobackend.support_material.repository;

import com.mrbeans.circulosestudiobackend.support_material.entity.SupportMaterialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ISupportMaterialRepository extends JpaRepository<SupportMaterialEntity, UUID> {
    boolean existsByTitle(String title);
    List<SupportMaterialEntity> findByCategory(String category);
    List<SupportMaterialEntity> findByWorkGroupId(UUID workgroupId);
    List<SupportMaterialEntity> findAllByWorkGroupId(UUID workGroupId);
}
