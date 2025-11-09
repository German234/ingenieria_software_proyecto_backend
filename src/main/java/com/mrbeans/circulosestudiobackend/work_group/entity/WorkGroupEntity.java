package com.mrbeans.circulosestudiobackend.work_group.entity;

import com.mrbeans.circulosestudiobackend.documents.entity.DocumentEntity;
import com.mrbeans.circulosestudiobackend.support_material.entity.SupportMaterialEntity;
import com.mrbeans.circulosestudiobackend.userXwork_group.entitiy.UserXWorkGroupEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "work_groups")
@Data
public class WorkGroupEntity {

    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "image_document_id", nullable = false)
    private DocumentEntity imageDocument;

    @Column(nullable = false, unique = true )
    private String slug;

    @OneToMany(mappedBy = "workGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserXWorkGroupEntity> userLinks = new ArrayList<>();

    public void setSlug() {
        String slug = name + "-" + UUID.randomUUID().toString().substring(0, 6);
        this.slug = slug;
    }
}
