package com.mrbeans.circulosestudiobackend.work_group.service.Impl;

import com.mrbeans.circulosestudiobackend.common.exception.GenericException;
import com.mrbeans.circulosestudiobackend.documents.entity.DocumentEntity;
import com.mrbeans.circulosestudiobackend.documents.repositories.IDocumentRepository;
import com.mrbeans.circulosestudiobackend.work_group.dtos.WorkGroupCreateDto;
import com.mrbeans.circulosestudiobackend.work_group.dtos.WorkGroupResponseDto;
import com.mrbeans.circulosestudiobackend.work_group.dtos.WorkGroupUpdateDto;
import com.mrbeans.circulosestudiobackend.work_group.entity.WorkGroupEntity;
import com.mrbeans.circulosestudiobackend.work_group.repositories.IWorkGroupsRepository;
import com.mrbeans.circulosestudiobackend.work_group.service.WorkGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WorkGroupServiceImpl implements WorkGroupService {

    @Autowired
    private IWorkGroupsRepository workGroupsRepository;

    @Autowired
    private IDocumentRepository documentRepository;

    @Override
    public WorkGroupEntity createWorkGroup(WorkGroupCreateDto workGroupCreateDto) {
        if (workGroupsRepository.existsByName(workGroupCreateDto.getName())) {
            throw new GenericException("Name already exists");
        }

        UUID bgDocId = workGroupCreateDto.getBackgroundDocumentId();
        DocumentEntity bgDoc = documentRepository.findById(bgDocId)
                .orElseThrow(() -> new GenericException("Background document not found: " + bgDocId));

        WorkGroupEntity workGroupEntity = new WorkGroupEntity();
        workGroupEntity.setName(workGroupCreateDto.getName());
        workGroupEntity.setImageDocument(bgDoc);

        workGroupEntity.setSlug();
        if (workGroupsRepository.existsBySlug(workGroupEntity.getSlug())) {
            throw new GenericException("Slug already exists");
        }

        workGroupsRepository.save(workGroupEntity);
        return workGroupEntity;
    }

    @Override
    @Transactional
    public WorkGroupResponseDto updateWorkGroup(UUID id, WorkGroupUpdateDto dto) {
        WorkGroupEntity entity = workGroupsRepository.findById(id)
                .orElseThrow(() -> new GenericException("Work group not found"));

        if (dto.getName() != null && !dto.getName().equals(entity.getName())) {
            String nuevoNombre = dto.getName();
            if (workGroupsRepository.existsByNameAndIdNot(nuevoNombre, id)) {
                throw new GenericException("Name already exists");
            }
            entity.setName(nuevoNombre);

            entity.setSlug();
            if (workGroupsRepository.existsBySlugAndIdNot(entity.getSlug(), id)) {
                throw new GenericException("Slug already exists");
            }
        }

        if (dto.getBackgroundDocumentId() != null) {
            UUID bgId = dto.getBackgroundDocumentId();
            DocumentEntity bg = documentRepository.findById(bgId)
                    .orElseThrow(() -> new GenericException("Background document not found: " + bgId));
            entity.setImageDocument(bg);
        }

        workGroupsRepository.saveAndFlush(entity);

        WorkGroupResponseDto resp = new WorkGroupResponseDto();
        resp.setId(entity.getId());
        resp.setNombre(entity.getName());
        resp.setBackgroundImage(entity.getImageDocument().getUrl());
        resp.setSlug(entity.getSlug());
        return resp;
    }

    @Override
    public List<WorkGroupResponseDto> findAllWorkGroups() {
        List<WorkGroupResponseDto> workGroups = workGroupsRepository.findAll().stream().map(workGroup -> {
            WorkGroupResponseDto dto = new WorkGroupResponseDto();
            dto.setId(workGroup.getId());
            dto.setNombre(workGroup.getName());
            dto.setBackgroundImage(
                    workGroup.getImageDocument().getUrl()
            );
            dto.setSlug(workGroup.getSlug());
            return dto;
        }).toList();

        if (workGroups.isEmpty()) {
            throw new GenericException("Ningun grupo de trabajo encontrado");
        }
        return workGroups;
    }

    @Override
    public WorkGroupResponseDto findBySlug(String slug) {
        WorkGroupEntity workGroupEntity = workGroupsRepository.findBySlug(slug);
        if (workGroupEntity == null) {
            throw new GenericException("Grupo de trabajo no encontrado con slug: " + slug);
        }
        WorkGroupResponseDto responseDto = new WorkGroupResponseDto();
        responseDto.setId(workGroupEntity.getId());
        responseDto.setNombre(workGroupEntity.getName());
        responseDto.setBackgroundImage(
                workGroupEntity.getImageDocument().getUrl()
        );
        return responseDto;
    }

    @Override
    public WorkGroupResponseDto findById(UUID id) {
        WorkGroupEntity workGroupEntity = workGroupsRepository.findById(id)
                .orElseThrow(() -> new GenericException("Work group not found"));
        WorkGroupResponseDto responseDto = new WorkGroupResponseDto();
        responseDto.setId(workGroupEntity.getId());
        responseDto.setNombre(workGroupEntity.getName());
        responseDto.setBackgroundImage(
                workGroupEntity.getImageDocument().getUrl()
        );
        responseDto.setSlug(workGroupEntity.getSlug());
        return responseDto;
    }

    @Override
    public void deleteWorkGroup(UUID id) {
        if (!workGroupsRepository.existsById(id)) {
            throw new GenericException("Work group not found");
        }
        workGroupsRepository.deleteById(id);
    }
}
