package com.mrbeans.circulosestudiobackend.documents.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "documents")
@Data
public class DocumentEntity {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false, unique = true)
    private String storedFilename;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String url;

}
