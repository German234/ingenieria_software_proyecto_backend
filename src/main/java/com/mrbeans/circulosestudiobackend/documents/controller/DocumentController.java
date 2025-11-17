package com.mrbeans.circulosestudiobackend.documents.controller;

import com.mrbeans.circulosestudiobackend.common.dto.SuccessResponse;
import com.mrbeans.circulosestudiobackend.documents.dtos.CreateDocumentDto;
import com.mrbeans.circulosestudiobackend.documents.dtos.ResponseDocumentDto;
import com.mrbeans.circulosestudiobackend.documents.dtos.UpdateDocumentDto;
import com.mrbeans.circulosestudiobackend.documents.services.impl.DocumentServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentServiceImpl documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<ResponseDocumentDto>> uploadDocument(
            @Valid CreateDocumentDto dto,
            @RequestPart("file") MultipartFile file
    ) {
        SuccessResponse<ResponseDocumentDto> response =
                new SuccessResponse<ResponseDocumentDto>(HttpStatus.CREATED.value(),"Documento subido correctamente",documentService.create(dto, file));
        return ResponseEntity.ok(response);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = ("/{id}"))
        public ResponseEntity<SuccessResponse<ResponseDocumentDto>> updateDocument(
                @PathVariable UUID id,
                @Valid UpdateDocumentDto dto,
                @RequestPart("file") MultipartFile file
        ) {

            SuccessResponse<ResponseDocumentDto> response =
                    new SuccessResponse<ResponseDocumentDto>(HttpStatus.OK.value(),"Documento actualizado correctamente", documentService.update(dto, id, file));
            return ResponseEntity.ok(response);
        }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<SuccessResponse<List<ResponseDocumentDto>>> getAllDocuments() {
        List<ResponseDocumentDto> documents = documentService.findAll();
        SuccessResponse<List<ResponseDocumentDto>> response =
                new SuccessResponse<>(HttpStatus.OK.value(),"Documentos obtenidos correctamente", documents);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ResponseDocumentDto>> getDocumentById(
            @PathVariable UUID id
    ) {
        ResponseDocumentDto document = documentService.findById(id);
        SuccessResponse<ResponseDocumentDto> response =
                new SuccessResponse<>(HttpStatus.OK.value(),"Documento encontrado correctamente", document);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteDocument(
            @PathVariable UUID id
    ) {
        documentService.delete(id);
        SuccessResponse<Void> response =
                new SuccessResponse<>(HttpStatus.OK.value(),"Documento eliminado correctamente", null);
        return ResponseEntity.ok(response);
    }
}
