package com.mrbeans.circulosestudiobackend.support_material.entity;

import com.mrbeans.circulosestudiobackend.comments.entity.CommentsEntity;
import com.mrbeans.circulosestudiobackend.documents_x_support_material.entity.DocumentXSupportMaterialEntity;
import com.mrbeans.circulosestudiobackend.work_group.entity.WorkGroupEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "support_material")
public class SupportMaterialEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "category", nullable = false)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workgroup_id", nullable = false)
    private WorkGroupEntity workGroup;

    @OneToMany(
            mappedBy = "supportMaterial",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DocumentXSupportMaterialEntity> documentsXSupportMaterials = new ArrayList<>();

    @OneToMany(
            mappedBy = "supportMaterial",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @OrderBy("createdAt ASC")
    private List<CommentsEntity> comments = new ArrayList<>();

}
