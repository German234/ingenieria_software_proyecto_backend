package com.mrbeans.circulosestudiobackend.role.services.impl;

import com.mrbeans.circulosestudiobackend.common.exception.GenericException;
import com.mrbeans.circulosestudiobackend.role.dtos.CreateRoleDto;
import com.mrbeans.circulosestudiobackend.role.dtos.ResponseRoleDto;
import com.mrbeans.circulosestudiobackend.role.entity.RoleEntity;
import com.mrbeans.circulosestudiobackend.role.repositories.IRoleRepository;
import com.mrbeans.circulosestudiobackend.role.services.RoleService;
import com.mrbeans.circulosestudiobackend.user.dtos.UserResponseDto;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private IRoleRepository roleRepository;

    @Override
    public RoleEntity findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public RoleEntity findById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new GenericException("Role not found"));
    }

    @Override
    public void createRole(CreateRoleDto createRoleDto) {
        if (roleRepository.existsByName(createRoleDto.getName())) {
            throw new GenericException("Role with this name already exists");
        }

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(createRoleDto.getName());

        roleRepository.save(roleEntity);
    }

    @Override
    public void deleteRole(UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new GenericException("Role not found");
        }
        roleRepository.deleteById(id);
    }

    @Override
    public List<ResponseRoleDto> findAll() {
        List<ResponseRoleDto> roles = roleRepository.findAll().stream()
                .map(this::toDto)
                .toList();

        if (roles.isEmpty()) {
            throw new GenericException("No hay ningun rol registrado");
        }
        return roles;
    }

    private ResponseRoleDto toDto(RoleEntity roleEntity) {
        ResponseRoleDto responseRoleDto = new ResponseRoleDto();
        responseRoleDto.setId(roleEntity.getId());
        responseRoleDto.setName(roleEntity.getName());
        return responseRoleDto;
    }
}
