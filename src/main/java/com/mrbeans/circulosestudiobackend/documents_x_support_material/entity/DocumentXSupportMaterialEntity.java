package com.mrbeans.circulosestudiobackend.documents_x_support_material.entity;

import com.mrbeans.circulosestudiobackend.documents.entity.DocumentEntity;
import com.mrbeans.circulosestudiobackend.support_material.entity.SupportMaterialEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "documents_x_support_material")
@Data
public class DocumentXSupportMaterialEntity implements Serializable {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private DocumentEntity document;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "support_material_id", nullable = false)
    private SupportMaterialEntity supportMaterial;
} 