package com.mrbeans.circulosestudiobackend.work_group.service;

import com.mrbeans.circulosestudiobackend.work_group.dtos.WorkGroupCreateDto;
import com.mrbeans.circulosestudiobackend.work_group.dtos.WorkGroupResponseDto;
import com.mrbeans.circulosestudiobackend.work_group.dtos.WorkGroupUpdateDto;
import com.mrbeans.circulosestudiobackend.work_group.entity.WorkGroupEntity;

import java.util.List;
import java.util.UUID;

public interface WorkGroupService {

    WorkGroupEntity createWorkGroup(WorkGroupCreateDto workGroupCreateDto);
    WorkGroupResponseDto updateWorkGroup(UUID id, WorkGroupUpdateDto workGroupUpdateDto);
    WorkGroupResponseDto findBySlug(String slug);
    WorkGroupResponseDto findById(UUID id);
    List<WorkGroupResponseDto> findAllWorkGroups();
    void deleteWorkGroup(UUID id);

}
