package com.mrbeans.circulosestudiobackend.comments.repository;

import com.mrbeans.circulosestudiobackend.comments.entity.CommentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ICommentRepository extends JpaRepository<CommentsEntity, UUID> {
    List<CommentsEntity> findBySupportMaterialIdOrderByCreatedAtDesc(UUID supportMaterialId);
    
    @Query("SELECT COUNT(c) FROM CommentsEntity c WHERE c.supportMaterial.id = :supportMaterialId")
    long countBySupportMaterialId(UUID supportMaterialId);
}