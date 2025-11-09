package com.mrbeans.circulosestudiobackend.documents.repositories;

import com.mrbeans.circulosestudiobackend.documents.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IDocumentRepository extends JpaRepository<DocumentEntity, UUID> {


}
