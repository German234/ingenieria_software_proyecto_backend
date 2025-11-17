package com.mrbeans.circulosestudiobackend.support_material.service.Impl;

import com.mrbeans.circulosestudiobackend.common.exception.GenericException;
import com.mrbeans.circulosestudiobackend.comments.repository.ICommentRepository;
import com.mrbeans.circulosestudiobackend.support_material.dtos.SupportMaterialCreateDto;
import com.mrbeans.circulosestudiobackend.support_material.dtos.SupportMaterialResponseDto;
import com.mrbeans.circulosestudiobackend.support_material.dtos.SupportMaterialUpdateDto;
import com.mrbeans.circulosestudiobackend.support_material.entity.SupportMaterialEntity;
import com.mrbeans.circulosestudiobackend.support_material.repository.ISupportMaterialRepository;
import com.mrbeans.circulosestudiobackend.support_material.service.SupportMaterialService;
import com.mrbeans.circulosestudiobackend.work_group.entity.WorkGroupEntity;
import com.mrbeans.circulosestudiobackend.work_group.repositories.IWorkGroupsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SupportMaterialServiceImpl implements SupportMaterialService {

    @Autowired
    private com.mrbeans.circulosestudiobackend.support_material.repository.ISupportMaterialRepository supportMaterialRepository;

    @Autowired
    private IWorkGroupsRepository workGroupsRepository;
    
    @Autowired
    private ICommentRepository commentRepository;

    @Override
    public SupportMaterialEntity createSupportMaterial(SupportMaterialCreateDto supportMaterialCreateDto) {

        // Validar si el grupo de trabajo existe
        WorkGroupEntity workGroup = workGroupsRepository.findById(supportMaterialCreateDto.getWorkgroupId())
                .orElseThrow(() -> new GenericException("El grupo de trabajo no existe"));

        // Crear entidad de material de apoyo
        SupportMaterialEntity supportMaterial = new SupportMaterialEntity();
        supportMaterial.setTitle(supportMaterialCreateDto.getTitle());
        supportMaterial.setDescription(supportMaterialCreateDto.getDescription());
        supportMaterial.setCategory(supportMaterialCreateDto.getCategory());
        supportMaterial.setWorkGroup(workGroup);

        return supportMaterialRepository.save(supportMaterial);

    }

    @Override
    public SupportMaterialResponseDto updateSupportMaterial(UUID id, SupportMaterialUpdateDto supportMaterialUpdateDto) {
        // Buscar material de apoyo
        SupportMaterialEntity supportMaterial = supportMaterialRepository.findById(id)
                .orElseThrow(() -> new GenericException("Material de apoyo no encontrado"));

        // Actualizar campos si se proporcionan
        if (supportMaterialUpdateDto.getTitle() != null) {
            // Validar si el nuevo título ya existe y no es el mismo material
            if (!supportMaterial.getTitle().equals(supportMaterialUpdateDto.getTitle()) &&
                    supportMaterialRepository.existsByTitle(supportMaterialUpdateDto.getTitle())) {
                throw new GenericException("El título ya existe");
            }
            supportMaterial.setTitle(supportMaterialUpdateDto.getTitle());
        }

        if (supportMaterialUpdateDto.getDescription() != null) {
            supportMaterial.setDescription(supportMaterialUpdateDto.getDescription());
        }

        if (supportMaterialUpdateDto.getCategory() != null) {
            supportMaterial.setCategory(supportMaterialUpdateDto.getCategory());
        }

        if (supportMaterialUpdateDto.getWorkgroupId() != null) {
            WorkGroupEntity workGroup = workGroupsRepository.findById(supportMaterialUpdateDto.getWorkgroupId())
                    .orElseThrow(() -> new GenericException("El grupo de trabajo no existe"));
            supportMaterial.setWorkGroup(workGroup);
        }

        // Guardar entidad actualizada
        supportMaterialRepository.save(supportMaterial);

        // Retornar DTO de respuesta
        return mapEntityToDto(supportMaterial);
    }

    @Override
    public SupportMaterialResponseDto findById(UUID id) {
        SupportMaterialEntity supportMaterial = supportMaterialRepository.findById(id)
                .orElseThrow(() -> new GenericException("Material de apoyo no encontrado"));
        return mapEntityToDto(supportMaterial);
    }

    @Override
    public List<SupportMaterialResponseDto> findAllSupportMaterials() {
        List<SupportMaterialEntity> supportMaterials = supportMaterialRepository.findAll();
        if (supportMaterials.isEmpty()) {
            throw new GenericException("No hay materiales de apoyo registrados");
        }
        return supportMaterials.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupportMaterialResponseDto> findByCategory(String category) {
        List<SupportMaterialEntity> supportMaterials = supportMaterialRepository.findByCategory(category);
        if (supportMaterials.isEmpty()) {
            throw new GenericException("No hay materiales de apoyo para esta categoría");
        }
        return supportMaterials.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupportMaterialResponseDto> findByWorkGroupId(UUID workgroupId) {
        // Validar si el grupo de trabajo existe
        if (!workGroupsRepository.existsById(workgroupId)) {
            throw new GenericException("El grupo de trabajo no existe");
        }

        List<SupportMaterialEntity> supportMaterials = supportMaterialRepository.findByWorkGroupId(workgroupId);
        if (supportMaterials.isEmpty()) {
            throw new GenericException("No hay materiales de apoyo para este grupo de trabajo");
        }
        return supportMaterials.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSupportMaterial(UUID id) {
        SupportMaterialEntity sm = supportMaterialRepository.findById(id)
                .orElseThrow(() -> new GenericException("Material de apoyo no encontrado"));
        supportMaterialRepository.delete(sm);
    }

    private SupportMaterialResponseDto mapEntityToDto(SupportMaterialEntity entity) {
        SupportMaterialResponseDto dto = new SupportMaterialResponseDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setCategory(entity.getCategory());
        dto.setWorkgroupId(entity.getWorkGroup().getId());
        dto.setWorkgroupName(entity.getWorkGroup().getName());
        
        // Obtener el número de comentarios para este material de soporte
        long commentCount = commentRepository.countBySupportMaterialId(entity.getId());
        dto.setCommentCount(commentCount);
        
        return dto;
    }
}
