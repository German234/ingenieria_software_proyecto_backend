package com.mrbeans.circulosestudiobackend.documents.services.impl;

import com.mrbeans.circulosestudiobackend.common.exception.GenericException;
import com.mrbeans.circulosestudiobackend.documents.dtos.CreateDocumentDto;
import com.mrbeans.circulosestudiobackend.documents.dtos.ResponseDocumentDto;
import com.mrbeans.circulosestudiobackend.documents.dtos.UpdateDocumentDto;
import com.mrbeans.circulosestudiobackend.documents.entity.DocumentEntity;
import com.mrbeans.circulosestudiobackend.documents.repositories.IDocumentRepository;
import com.mrbeans.circulosestudiobackend.documents.services.DocumentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final IDocumentRepository documentRepository;
    private final Path rootLocation;
    private final String baseUrl;
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5 MB

    private static final Map<String,String> EXTENSION_MAP = Map.of(
            MediaType.APPLICATION_PDF_VALUE,   ".pdf",
            MediaType.IMAGE_PNG_VALUE,         ".png",
            MediaType.IMAGE_JPEG_VALUE,        ".jpg"
    );

    public DocumentServiceImpl(IDocumentRepository documentRepository,
                               @Value("${app.upload-dir}") String uploadDir,
                               @Value("${app.base-url}") String baseUrl) {
        this.documentRepository = documentRepository;
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.baseUrl = baseUrl;
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new GenericException("No se pudo crear el directorio de subida");
        }
    }

    @Override
    public ResponseDocumentDto create(CreateDocumentDto dto, MultipartFile file) {
        boolean onlyImages = "users".equalsIgnoreCase(dto.getCategory());
        validateFile(file, onlyImages);

        String extension = EXTENSION_MAP.get(file.getContentType());
        Path categoryDir = resolveCategoryDir(dto.getCategory());

        String storedFilename = dto.getOriginalFilename().toLowerCase()
                + "-" + UUID.randomUUID() + extension;

        writeFile(categoryDir, storedFilename, file);

        String url = buildUrl(dto.getCategory(), storedFilename);

        DocumentEntity doc = new DocumentEntity();
        doc.setOriginalFilename(dto.getOriginalFilename());
        doc.setStoredFilename(storedFilename);
        doc.setCategory(dto.getCategory());
        doc.setUrl(url);
        DocumentEntity saved = documentRepository.save(doc);
        return toDto(saved);
    }

    @Override
    public List<ResponseDocumentDto> findAll() {
        List<ResponseDocumentDto> documents = documentRepository.findAll().stream().map( this::toDto).toList();

        if (documents.isEmpty()) {
            throw new GenericException("No se encontraron documentos");
        }
        return documents;
    }

    @Override
    public ResponseDocumentDto findById(UUID id) {
        DocumentEntity doc = documentRepository.findById(id)
                .orElseThrow(() -> new GenericException("Documento no encontrado"));
        return toDto(doc);
    }

    @Override
    public void delete(UUID id) {
        DocumentEntity doc = documentRepository.findById(id)
                .orElseThrow(() -> new GenericException("Documento no encontrado"));
        deleteOldFile(doc);
        documentRepository.delete(doc);
    }

    @Override
    public ResponseDocumentDto update(UpdateDocumentDto dto, UUID id, MultipartFile file) {
        DocumentEntity doc = documentRepository.findById(id)
                .orElseThrow(() -> new GenericException("Documento no encontrado"));

        if (dto.getOriginalFilename() != null) {
            doc.setOriginalFilename(dto.getOriginalFilename());
        }
        if (dto.getCategory() != null) {
            doc.setCategory(dto.getCategory());
        }

        if (file != null && !file.isEmpty()) {
            boolean onlyImages = "users".equalsIgnoreCase(doc.getCategory());
            validateFile(file, onlyImages);

            String extension = EXTENSION_MAP.get(file.getContentType());
            Path categoryDir = resolveCategoryDir(doc.getCategory());

            String storedFilename = doc.getOriginalFilename().toLowerCase() + "-" + UUID.randomUUID() + extension;
            writeFile(categoryDir, storedFilename, file);

            deleteOldFile(doc);

            doc.setStoredFilename(storedFilename);
            doc.setUrl(buildUrl(doc.getCategory(), storedFilename));
        }

        DocumentEntity updated = documentRepository.save(doc);
        return toDto(updated);
    }

    private void validateFile(MultipartFile file, boolean onlyImages) {
        if (file.isEmpty()) {
            throw new GenericException("El archivo es obligatorio");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new GenericException("El tamaño no puede exceder 5 MB");
        }
        String ct = file.getContentType();
        if (!EXTENSION_MAP.containsKey(ct)) {
            throw new GenericException("Formato no soportado. Solo PDF, PNG y JPG");
        }
        if (onlyImages && MediaType.APPLICATION_PDF_VALUE.equals(ct)) {
            throw new GenericException("En la categoría 'users' solo se permiten imágenes JPG o PNG");
        }
    }

    private Path resolveCategoryDir(String category) {
        Path dir = rootLocation.resolve(category);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new GenericException("Error al crear directorio");
        }
        return dir;
    }

    private void writeFile(Path dir, String filename, MultipartFile file) {
        try {
            Files.write(dir.resolve(filename), file.getBytes(), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new GenericException("Error al guardar archivo");
        }
    }

    private String buildUrl(String category, String filename) {
        return String.format("%s/uploads/%s/%s", baseUrl, category, filename);
    }

    private void deleteOldFile(DocumentEntity doc) {
        Path oldFile = rootLocation.resolve(doc.getCategory()).resolve(doc.getStoredFilename());
        try {
            Files.deleteIfExists(oldFile);
        } catch (IOException e) {
            throw new GenericException("Error al eliminar el archivo físico anterior");
        }
    }

    private ResponseDocumentDto toDto(DocumentEntity doc) {
        ResponseDocumentDto dto = new ResponseDocumentDto();
        dto.setId(doc.getId());
        dto.setCategory(doc.getCategory());
        dto.setOriginalFilename(doc.getOriginalFilename());
        dto.setUrl(doc.getUrl());
        return dto;
    }
}
