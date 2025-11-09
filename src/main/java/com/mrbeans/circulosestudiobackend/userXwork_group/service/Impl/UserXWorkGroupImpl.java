
package com.mrbeans.circulosestudiobackend.userXwork_group.service.Impl;

import com.mrbeans.circulosestudiobackend.common.exception.GenericException;
import com.mrbeans.circulosestudiobackend.documents_x_support_material.dto.ResponseSupportMaterialDto;
import com.mrbeans.circulosestudiobackend.documents_x_support_material.service.DocumentXSupportMaterialService;
import com.mrbeans.circulosestudiobackend.user.entity.UserEntity;
import com.mrbeans.circulosestudiobackend.user.repositories.IUserRepository;
import com.mrbeans.circulosestudiobackend.userXwork_group.dtos.*;
import com.mrbeans.circulosestudiobackend.userXwork_group.entitiy.UserXWorkGroupEntity;
import com.mrbeans.circulosestudiobackend.userXwork_group.repositories.IUserXWorkGroupRepository;
import com.mrbeans.circulosestudiobackend.userXwork_group.service.UserXWorkGroupService;
import com.mrbeans.circulosestudiobackend.work_group.dtos.WorkGroupCreateDto;
import com.mrbeans.circulosestudiobackend.work_group.dtos.WorkGroupResponseDto;
import com.mrbeans.circulosestudiobackend.work_group.dtos.WorkGroupUpdateDto;
import com.mrbeans.circulosestudiobackend.work_group.entity.WorkGroupEntity;
import com.mrbeans.circulosestudiobackend.work_group.repositories.IWorkGroupsRepository;
import com.mrbeans.circulosestudiobackend.work_group.service.WorkGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserXWorkGroupImpl implements UserXWorkGroupService {

    @Autowired
    private IUserXWorkGroupRepository userXWorkGroupRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IWorkGroupsRepository workGroupsRepository;

    @Autowired
    private WorkGroupService workGroupService;

    @Autowired
    private DocumentXSupportMaterialService documentXSupportMaterialService;

    @Override
    @Transactional
    public void createUserWorkGroup(CreateUserWorkGroupDto dto) {
        WorkGroupCreateDto wgDto = new WorkGroupCreateDto();
        wgDto.setName(dto.getName());
        wgDto.setBackgroundDocumentId(dto.getBackgroundImageId());
        WorkGroupEntity wg = workGroupService.createWorkGroup(wgDto);

        AssingUserWorkGroupDto assignDto = new AssingUserWorkGroupDto();
        assignDto.setWorkGroupId(wg.getId());
        assignDto.setUserIds(dto.getUserIds());

        assignUsersToWorkGroup(assignDto);
    }

    @Override
    @Transactional
    public void assignUsersToWorkGroup(AssingUserWorkGroupDto dto) {
        WorkGroupEntity wg = workGroupsRepository.findById(dto.getWorkGroupId())
                .orElseThrow(() -> new GenericException("Work group no encontrado"));

        var enlaces = dto.getUserIds().stream().map(userId -> {
            if (userXWorkGroupRepository.existsByUserIdAndWorkGroupId(userId, dto.getWorkGroupId())) {
                throw new GenericException(
                        String.format("El usuario %s ya está en el grupo %s", userId, dto.getWorkGroupId()));
            }
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new GenericException("Usuario no encontrado: " + userId));

            UserXWorkGroupEntity link = new UserXWorkGroupEntity();
            link.setUser(user);
            link.setWorkGroup(wg);
            return link;
        }).toList();

        userXWorkGroupRepository.saveAll(enlaces);
    }

    @Override
    @Transactional
    public void deleteUserXWorkGroup(UUID userId, UUID workGroupId) {
        if (!userXWorkGroupRepository.existsByUserIdAndWorkGroupId(userId, workGroupId)) {
            throw new GenericException("El usuario no está asignado a este grupo de trabajo");
        }
        userXWorkGroupRepository.deleteUserXworkGroupEntitiesByUserIdAndWorkGroupId(userId, workGroupId);
    }

    @Override
    @Transactional
    public void updateUserWorkGroup(UUID workGroupId, UpdateUserWorkGroupDto dto) {
        WorkGroupUpdateDto wgUpdateDto = new WorkGroupUpdateDto();
        wgUpdateDto.setName(dto.getName());
        wgUpdateDto.setBackgroundDocumentId(dto.getBackgroundImageId());
        workGroupService.updateWorkGroup(workGroupId, wgUpdateDto);

        List<UUID> currentUserIds = userXWorkGroupRepository
                .findAllByWorkGroupId(workGroupId)
                .stream()
                .map(link -> link.getUser().getId())
                .toList();

        Set<UUID> newUserIds = new HashSet<>(dto.getUserIds());
        List<UUID> toRemove = currentUserIds.stream()
                .filter(id -> !newUserIds.contains(id))
                .toList();
        List<UUID> toAdd = newUserIds.stream()
                .filter(id -> !currentUserIds.contains(id))
                .toList();

        for (UUID userId : toRemove) {
            userXWorkGroupRepository
                    .deleteUserXworkGroupEntitiesByUserIdAndWorkGroupId(userId, workGroupId);
        }

        if (!toAdd.isEmpty()) {
            AssingUserWorkGroupDto assignDto = new AssingUserWorkGroupDto();
            assignDto.setWorkGroupId(workGroupId);
            assignDto.setUserIds(toAdd);
            assignUsersToWorkGroup(assignDto);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkGroupResponseDto> getWorkGroupsByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new GenericException("User not found");
        }
        var dtos = userXWorkGroupRepository.findAllByUserId(userId).stream()
                .map(this::toWorkGroupDto)
                .toList();
        if (dtos.isEmpty()) {
            throw new GenericException("Ningún grupo de trabajo encontrado para este usuario");
        }
        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseWorkGroupDto> getUsersByWorkGroupId(UUID workGroupId) {
        if (!workGroupsRepository.existsById(workGroupId)) {
            throw new GenericException("Work group not found");
        }
        var dtos = userXWorkGroupRepository.findAllByWorkGroupId(workGroupId).stream()
                .map(link -> toDto(link.getUser()))
                .toList();
        if (dtos.isEmpty()) {
            throw new GenericException("Ningún usuario encontrado para este grupo de trabajo");
        }
        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseWorkGroupDto> getAllStudentsByWorkGroupId(UUID workGroupId) {
        var enlaces = userXWorkGroupRepository
                .findByWorkGroupId_AndUser_Role_Name(workGroupId, "ALUMNO");
        var dtos = enlaces.stream()
                .map(link -> toDto(link.getUser()))
                .toList();
        if (dtos.isEmpty()) {
            throw new GenericException("No hay alumnos en este grupo de trabajo");
        }
        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseWorkGroupDto> getAllTutorsByWorkGroupId(UUID workGroupId) {
        var enlaces = userXWorkGroupRepository
                .findByWorkGroupId_AndUser_Role_Name(workGroupId, "TUTOR");
        var dtos = enlaces.stream()
                .map(link -> toDto(link.getUser()))
                .toList();
        if (dtos.isEmpty()) {
            throw new GenericException("No hay tutores en este grupo de trabajo");
        }
        return dtos;
    }

    @Override
    public List<UserResponseWorkGroupDto> getAllTutorsWithWorkgroups() {
        var users = userRepository.findByRoleName("TUTOR");
        if (users.isEmpty()) {
            throw new GenericException("No hay tutores registrados");
        }
        return users.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<UserResponseWorkGroupDto> getAllAlumnosWithWorkgroups() {
        var users = userRepository.findByRoleName("ALUMNO");
        if (users.isEmpty()) {
            throw new GenericException("No hay tutores registrados");
        }
        return users.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseWorkGroupDto getByWorkGroupSlug(String slug) {
        WorkGroupResponseDto wgDto = workGroupService.findBySlug(slug);
        UUID wgId = wgDto.getId();

        List<UserDto> alumnos = userXWorkGroupRepository
                .findByWorkGroupId_AndUser_Role_Name(wgId, "ALUMNO")
                .stream()
                .map(link -> {
                    UserEntity u = link.getUser();
                    UserDto dto = new UserDto();
                    dto.setUserXWorkgroupId(link.getId());
                    dto.setId(u.getId());
                    dto.setNombre(u.getName());
                    dto.setEmail(u.getEmail());
                    dto.setImage(u.getImageDocument().getUrl());
                    return dto;
                })
                .toList();
        List<UserDto> tutors = userXWorkGroupRepository
                .findByWorkGroupId_AndUser_Role_Name(wgId, "TUTOR")
                .stream()
                .map(link -> {
                    UserEntity u = link.getUser();
                    UserDto dto = new UserDto();
                    dto.setUserXWorkgroupId(link.getId());
                    dto.setId(u.getId());
                    dto.setNombre(u.getName());
                    dto.setEmail(u.getEmail());
                    dto.setImage(u.getImageDocument().getUrl());
                    return dto;
                })
                .toList();

        List<ResponseSupportMaterialDto> supportMaterials =
                documentXSupportMaterialService.getSupportMaterialsByWorkgroupId(wgId);

        ResponseWorkGroupDto response = new ResponseWorkGroupDto();
        response.setId(wgDto.getId());
        response.setName(wgDto.getNombre());
        response.setBackgroundImage(wgDto.getBackgroundImage());
        response.setAlumnos(alumnos);
        response.setTutors(tutors);
        response.setFiles(supportMaterials);

        return response;
    }

    private UserResponseWorkGroupDto toDto(UserEntity user) {
        var dto = new UserResponseWorkGroupDto();
        dto.setId(user.getId().toString());
        dto.setNombre(user.getName());
        dto.setEmail(user.getEmail());
        dto.setImage(user.getImageDocument().getUrl());
        dto.setActive(user.isActive());

        var grupos = user.getWorkGroupLinks().stream()
                .map(link -> link.getWorkGroup().getName())
                .toList();
        dto.setWorkgroups(grupos);
        return dto;
    }

    private WorkGroupResponseDto toWorkGroupDto(UserXWorkGroupEntity link) {
        var wg = link.getWorkGroup();
        var dto = new WorkGroupResponseDto();
        dto.setId(wg.getId());
        dto.setNombre(wg.getName());
        dto.setSlug(wg.getSlug());
        dto.setBackgroundImage(wg.getImageDocument().getUrl());
        return dto;
    }
}
