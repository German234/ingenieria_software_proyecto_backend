package com.mrbeans.circulosestudiobackend.role.services;

import com.mrbeans.circulosestudiobackend.role.dtos.CreateRoleDto;
import com.mrbeans.circulosestudiobackend.role.dtos.ResponseRoleDto;
import com.mrbeans.circulosestudiobackend.role.entity.RoleEntity;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    RoleEntity findByName(String name);
    List<ResponseRoleDto> findAll();
    RoleEntity findById(UUID id);
    void createRole(CreateRoleDto createRoleDto);
    void deleteRole(UUID id);
}
