package com.mrbeans.circulosestudiobackend.documents.services;

import com.mrbeans.circulosestudiobackend.documents.dtos.CreateDocumentDto;
import com.mrbeans.circulosestudiobackend.documents.dtos.ResponseDocumentDto;
import com.mrbeans.circulosestudiobackend.documents.dtos.UpdateDocumentDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface DocumentService {

    ResponseDocumentDto create(CreateDocumentDto dto, MultipartFile file);

    List<ResponseDocumentDto> findAll();

    ResponseDocumentDto findById(UUID id);

    void delete(UUID id);

    ResponseDocumentDto update(UpdateDocumentDto dto, UUID id, MultipartFile file);
}