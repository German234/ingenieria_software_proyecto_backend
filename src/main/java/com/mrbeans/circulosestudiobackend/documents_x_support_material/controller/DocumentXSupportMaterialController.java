package com.mrbeans.circulosestudiobackend.documents_x_support_material.controller;

import com.mrbeans.circulosestudiobackend.common.dto.SuccessResponse;
import com.mrbeans.circulosestudiobackend.documents_x_support_material.dto.*;
import com.mrbeans.circulosestudiobackend.documents_x_support_material.service.DocumentXSupportMaterialService;
import com.mrbeans.circulosestudiobackend.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents-support-materials")
public class DocumentXSupportMaterialController {

    @Autowired
    private DocumentXSupportMaterialService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TUTOR')")
    public ResponseEntity<SuccessResponse<Void>> createSupportMaterial(
            @Valid @RequestBody CreateDocumentXSupportMaterial dto,
            @AuthenticationPrincipal CustomUserPrincipal principal

    ) {
        service.createSupportMaterialXDocument(principal.getName(), dto);
        SuccessResponse<Void> resp = new SuccessResponse<>(
                HttpStatus.CREATED.value(),
                "Material de apoyo creado satisfactoriamente",
                null
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resp);
    }

//    @DeleteMapping("/{supportMaterialId}/documents/{documentId}")
//    @PreAuthorize("hasAnyRole('ADMIN','TUTOR')")
//    public ResponseEntity<SuccessResponse<Void>> removeDocument(
//            @PathVariable UUID supportMaterialId,
//            @PathVariable UUID documentId
//    ) {
//        service.deleteDocumentFromSupportMaterial(documentId, supportMaterialId);
//        SuccessResponse<Void> resp = new SuccessResponse<>(
//                HttpStatus.OK.value(),
//                "Documento removido del material de apoyo satisfactoriamente",
//                null
//        );
//        return ResponseEntity.ok(resp);
//    }

    @PatchMapping("/support-material/{supportMaterialId}")
    @PreAuthorize("hasAnyRole('ADMIN','TUTOR')")
    public ResponseEntity<SuccessResponse<Void>> updateSupportMaterial(
            @PathVariable UUID supportMaterialId,
            @Valid @RequestBody UpdateDocumentXSupportMaterialDto dto
    ) {
        service.updateSupportMaterial(supportMaterialId, dto);
        SuccessResponse<Void> resp = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Material de apoyo actualizado satisfactoriamente",
                null
        );
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/workgroup/{workGroupId}")
    public ResponseEntity<SuccessResponse<List<ResponseSupportMaterialDto>>> getSupportMaterialsByWorkgroup(
            @PathVariable UUID workGroupId
    ) {
        List<ResponseSupportMaterialDto> data = service.getSupportMaterialsByWorkgroupId(workGroupId);
        SuccessResponse<List<ResponseSupportMaterialDto>> resp = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Materiales de apoyo encontrados",
                data
        );
        return ResponseEntity.ok(resp);
    }
}