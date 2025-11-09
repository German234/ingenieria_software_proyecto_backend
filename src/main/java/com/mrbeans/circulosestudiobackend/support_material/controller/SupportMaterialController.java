package com.mrbeans.circulosestudiobackend.support_material.controller;

import com.mrbeans.circulosestudiobackend.common.dto.SuccessResponse;
import com.mrbeans.circulosestudiobackend.support_material.dtos.SupportMaterialCreateDto;
import com.mrbeans.circulosestudiobackend.support_material.dtos.SupportMaterialResponseDto;
import com.mrbeans.circulosestudiobackend.support_material.dtos.SupportMaterialUpdateDto;
import com.mrbeans.circulosestudiobackend.support_material.service.SupportMaterialService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/support-materials")
public class SupportMaterialController {

    @Autowired
    private SupportMaterialService supportMaterialService;

//    @PostMapping
//    public ResponseEntity<SuccessResponse<Void>> createSupportMaterial(@Valid @RequestBody SupportMaterialCreateDto supportMaterialCreateDto) {
//        supportMaterialService.createSupportMaterial(supportMaterialCreateDto);
//        SuccessResponse<Void> response = new SuccessResponse<>(HttpStatus.CREATED.value(),"Material de apoyo creado exitosamente", null);
//        return ResponseEntity.ok(response);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<SuccessResponse<SupportMaterialResponseDto>> updateSupportMaterial(
//            @PathVariable UUID id, @RequestBody SupportMaterialUpdateDto supportMaterialUpdateDto) {
//        SupportMaterialResponseDto updatedMaterial = supportMaterialService.updateSupportMaterial(id, supportMaterialUpdateDto);
//        SuccessResponse<SupportMaterialResponseDto> response = new SuccessResponse<>(HttpStatus.OK.value(),"Material de apoyo actualizado exitosamente", updatedMaterial);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<SuccessResponse<SupportMaterialResponseDto>> getSupportMaterialById(@PathVariable UUID id) {
//        SupportMaterialResponseDto material = supportMaterialService.findById(id);
//        SuccessResponse<SupportMaterialResponseDto> response = new SuccessResponse<>(HttpStatus.OK.value(),"Material de apoyo encontrado", material);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping
//    public ResponseEntity<SuccessResponse<List<SupportMaterialResponseDto>>> getAllSupportMaterials() {
//        List<SupportMaterialResponseDto> materials = supportMaterialService.findAllSupportMaterials();
//        SuccessResponse<List<SupportMaterialResponseDto>> response = new SuccessResponse<>(HttpStatus.OK.value(),"Materiales de apoyo encontrados", materials);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/category/{category}")
//    public ResponseEntity<SuccessResponse<List<SupportMaterialResponseDto>>> getSupportMaterialsByCategory(@PathVariable String category) {
//        List<SupportMaterialResponseDto> materials = supportMaterialService.findByCategory(category);
//        SuccessResponse<List<SupportMaterialResponseDto>> response = new SuccessResponse<>(HttpStatus.OK.value(),"Materiales de apoyo encontrados para la categoría", materials);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/workgroup/{workgroupId}")
//    public ResponseEntity<SuccessResponse<List<SupportMaterialResponseDto>>> getSupportMaterialsByWorkGroup(@PathVariable UUID workgroupId) {
//        List<SupportMaterialResponseDto> materials = supportMaterialService.findByWorkGroupId(workgroupId);
//        SuccessResponse<List<SupportMaterialResponseDto>> response = new SuccessResponse<>(HttpStatus.OK.value(),"Materiales de apoyo encontrados para el grupo de trabajo", materials);
//        return ResponseEntity.ok(response);
//    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<SuccessResponse<Void>> deleteSupportMaterial(@PathVariable UUID id) {
        supportMaterialService.deleteSupportMaterial(id);
        SuccessResponse<Void> response = new SuccessResponse<>(HttpStatus.OK.value(),"Material de apoyo eliminado exitosamente", null);
        return ResponseEntity.ok(response);
    }
}