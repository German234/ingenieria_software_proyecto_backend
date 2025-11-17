package com.mrbeans.circulosestudiobackend.comments.entity;

import com.mrbeans.circulosestudiobackend.user.entity.UserEntity;
import com.mrbeans.circulosestudiobackend.support_material.entity.SupportMaterialEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Data
public class CommentsEntity {

    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "support_material_id", nullable = false)
    private SupportMaterialEntity supportMaterial;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
