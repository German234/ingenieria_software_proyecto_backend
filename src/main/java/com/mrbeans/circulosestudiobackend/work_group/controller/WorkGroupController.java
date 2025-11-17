package com.mrbeans.circulosestudiobackend.work_group.controller;

import com.mrbeans.circulosestudiobackend.common.dto.SuccessResponse;
import com.mrbeans.circulosestudiobackend.work_group.dtos.WorkGroupCreateDto;
import com.mrbeans.circulosestudiobackend.work_group.dtos.WorkGroupResponseDto;
import com.mrbeans.circulosestudiobackend.work_group.dtos.WorkGroupUpdateDto;
import com.mrbeans.circulosestudiobackend.work_group.service.Impl.WorkGroupServiceImpl;
import com.mrbeans.circulosestudiobackend.work_group.service.WorkGroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/work-groups")
public class WorkGroupController {

    @Autowired
    private WorkGroupService workGroupService;

//    @PostMapping
//    public ResponseEntity<SuccessResponse<Void>> createWorkGroup(@Valid @RequestBody WorkGroupCreateDto workGroupCreateDto) {
//        workGroupService.createWorkGroup(workGroupCreateDto);
//        SuccessResponse<Void> response = new SuccessResponse<>(HttpStatus.CREATED.value(),"Work group created successfully", null);
//        return ResponseEntity.ok(response);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<SuccessResponse<WorkGroupResponseDto>> updateWorkGroup(
//            @PathVariable UUID id, @RequestBody WorkGroupUpdateDto workGroupUpdateDto) {
//        workGroupService.updateWorkGroup(id, workGroupUpdateDto);
//        SuccessResponse<WorkGroupResponseDto> response = new SuccessResponse<>(HttpStatus.OK.value(),"Work group updated successfully", null);
//        return ResponseEntity.ok(response);
//    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<Void>> deleteWorkGroup(@PathVariable UUID id) {
        workGroupService.deleteWorkGroup(id);
        SuccessResponse<Void> response = new SuccessResponse<>(HttpStatus.OK.value(),"Work group deleted successfully", null);
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/slug/{slug}")
//    public ResponseEntity<SuccessResponse<WorkGroupResponseDto>> getWorkGroupBySlug(@PathVariable String slug) {
//        WorkGroupResponseDto workGroup = workGroupService.findBySlug(slug);
//        SuccessResponse<WorkGroupResponseDto> response = new SuccessResponse<>(HttpStatus.OK.value(),"Work group found", workGroup);
//        return ResponseEntity.ok(response);
//    }
//
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<WorkGroupResponseDto>>> getAllWorkGroups() {
        List<WorkGroupResponseDto> workGroups = workGroupService.findAllWorkGroups();
        SuccessResponse<List<WorkGroupResponseDto>> response = new SuccessResponse<>(HttpStatus.OK.value(),"All work groups retrieved successfully", workGroups);
        return ResponseEntity.ok(response);
    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<SuccessResponse<WorkGroupResponseDto>> getWorkGroupById(@PathVariable UUID id) {
//        WorkGroupResponseDto workGroup = workGroupService.findById(id);
//        SuccessResponse<WorkGroupResponseDto> response = new SuccessResponse<>(HttpStatus.OK.value(),"Work group found", workGroup);
//        return ResponseEntity.ok(response);
//    }
}
