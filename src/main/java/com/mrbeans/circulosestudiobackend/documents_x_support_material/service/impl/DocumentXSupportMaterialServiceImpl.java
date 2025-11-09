package com.mrbeans.circulosestudiobackend.documents_x_support_material.service.impl;

import com.mrbeans.circulosestudiobackend.common.exception.GenericException;
import com.mrbeans.circulosestudiobackend.comments.repository.ICommentRepository;
import com.mrbeans.circulosestudiobackend.documents.entity.DocumentEntity;
import com.mrbeans.circulosestudiobackend.documents.repositories.IDocumentRepository;
import com.mrbeans.circulosestudiobackend.documents_x_support_material.dto.*;
import com.mrbeans.circulosestudiobackend.documents_x_support_material.entity.DocumentXSupportMaterialEntity;
import com.mrbeans.circulosestudiobackend.documents_x_support_material.repository.IDocumentXSupportMaterialRepository;
import com.mrbeans.circulosestudiobackend.documents_x_support_material.service.DocumentXSupportMaterialService;
import com.mrbeans.circulosestudiobackend.support_material.dtos.SupportMaterialCreateDto;
import com.mrbeans.circulosestudiobackend.support_material.entity.SupportMaterialEntity;
import com.mrbeans.circulosestudiobackend.support_material.repository.ISupportMaterialRepository;
import com.mrbeans.circulosestudiobackend.support_material.service.SupportMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentXSupportMaterialServiceImpl implements DocumentXSupportMaterialService {

    @Autowired
    private ISupportMaterialRepository supportMaterialRepository;

    @Autowired
    private IDocumentRepository documentRepository;

    @Autowired
    private IDocumentXSupportMaterialRepository dxsmRepository;

    @Autowired
    private SupportMaterialService supportMaterialService;
    
    @Autowired
    private ICommentRepository commentRepository;

    @Override
    @Transactional
    public void createSupportMaterialXDocument(String UserName, CreateDocumentXSupportMaterial dto) {

        SupportMaterialCreateDto smdto = new SupportMaterialCreateDto();
        smdto.setTitle(UserName + " ha publicado un nuevo material de apoyo");
        smdto.setDescription(dto.getDescripcion());
        smdto.setCategory(dto.getCategoria());
        smdto.setWorkgroupId(dto.getWorkgroupId());
        SupportMaterialEntity NewSm = supportMaterialService.createSupportMaterial(smdto);

        AssignDocumentsDto assignDto = new AssignDocumentsDto();
        assignDto.setSupportMaterialId(NewSm.getId());
        assignDto.setDocumentIds(dto.getDocumentIds());
        assignDocumentsToSupportMaterial(assignDto);
    }

    @Override
    @Transactional
    public void assignDocumentsToSupportMaterial(AssignDocumentsDto dto) {
        SupportMaterialEntity sm = supportMaterialRepository.findById(dto.getSupportMaterialId())
                .orElseThrow(() -> new GenericException("Material de apoyo no encontrado: " + dto.getSupportMaterialId()));

        List<DocumentXSupportMaterialEntity> enlaces = dto.getDocumentIds().stream().map(docId -> {
            if (dxsmRepository.existsByDocumentIdAndSupportMaterialId(docId, dto.getSupportMaterialId())) {
                throw new GenericException(
                        String.format("El documento %s ya está asignado al material %s", docId, dto.getSupportMaterialId())
                );
            }
            DocumentEntity doc = documentRepository.findById(docId)
                    .orElseThrow(() -> new GenericException("Documento no encontrado: " + docId));

            DocumentXSupportMaterialEntity link = new DocumentXSupportMaterialEntity();
            link.setDocument(doc);
            link.setSupportMaterial(sm);
            return link;
        }).collect(Collectors.toList());

        dxsmRepository.saveAll(enlaces);
    }

    @Override
    @Transactional
    public void deleteDocumentFromSupportMaterial(UUID supportMaterialId, UUID documentId) {
        if (!dxsmRepository.existsByDocumentIdAndSupportMaterialId(documentId, supportMaterialId)) {
            throw new GenericException("La relación documento-material no existe");
        }
        dxsmRepository.deleteByDocumentIdAndSupportMaterialId(documentId, supportMaterialId);
    }

    @Override
    @Transactional
    public void updateSupportMaterial(UUID supportMaterialId, UpdateDocumentXSupportMaterialDto dto) {

        SupportMaterialEntity sm = supportMaterialRepository.findById(supportMaterialId)
                .orElseThrow(() -> new GenericException("Material de apoyo no encontrado: " + supportMaterialId));

        if (dto.getTitle() != null)      sm.setTitle(dto.getTitle());
        if (dto.getDescripcion() != null) sm.setDescription(dto.getDescripcion());
        if (dto.getCategoria() != null)   sm.setCategory(dto.getCategoria());
        supportMaterialRepository.save(sm);

        List<UUID> actuales = dxsmRepository.findAllBySupportMaterialId(supportMaterialId).stream()
                .map(link -> link.getDocument().getId())
                .collect(Collectors.toList());

        Set<UUID> nuevos = new HashSet<>(dto.getDocumentIds());
        List<UUID> toRemove = actuales.stream().filter(id -> !nuevos.contains(id)).toList();
        List<UUID> toAdd    = nuevos.stream().filter(id -> !actuales.contains(id)).toList();

        toRemove.forEach(docId ->
                dxsmRepository.deleteByDocumentIdAndSupportMaterialId(docId, supportMaterialId)
        );

        if (!toAdd.isEmpty()) {
            AssignDocumentsDto assignDto = new AssignDocumentsDto();
            assignDto.setSupportMaterialId(supportMaterialId);
            assignDto.setDocumentIds(toAdd);
            assignDocumentsToSupportMaterial(assignDto);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSupportMaterialDto> getSupportMaterialsByWorkgroupId(UUID workGroupId) {
        List<SupportMaterialEntity> listSm = supportMaterialRepository
                .findAllByWorkGroupId(workGroupId);

        if (listSm.isEmpty()) {
            return Collections.emptyList();
        }

        return listSm.stream().map(sm -> {
            ResponseSupportMaterialDto resp = new ResponseSupportMaterialDto();
            resp.setSupportMaterialId(sm.getId());
            resp.setTitulo(sm.getTitle());
            resp.setDescripcion(sm.getDescription());
            resp.setCategoria(sm.getCategory());

            List<FilesDto> files = dxsmRepository.findAllBySupportMaterialId(sm.getId()).stream()
                    .map(link -> {
                        DocumentEntity d = link.getDocument();
                        FilesDto f = new FilesDto();
                        f.setId(d.getId());
                        f.setOriginalFilename(d.getOriginalFilename());
                        f.setUrl(d.getUrl());
                        f.setContentType(deriveFileType(d.getStoredFilename()));
                        return f;
                    }).toList();
            resp.setFiles(files);
            
            // Obtener el número de comentarios para este material de soporte
            long commentCount = commentRepository.countBySupportMaterialId(sm.getId());
            resp.setCommentCount(commentCount);

            return resp;
        }).toList();
    }

    private String deriveFileType(String filename) {
        String ext = "";
        int i = filename.lastIndexOf('.');
        if (i >= 0) {
            ext = filename.substring(i + 1).toLowerCase();
        }
        return switch (ext) {
            case "png", "jpg", "jpeg" -> "imagen";
            case "pdf"                -> "documento";
            default                   -> "otro";
        };
    }
}