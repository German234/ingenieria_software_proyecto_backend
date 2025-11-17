package com.mrbeans.circulosestudiobackend.role.controllers;

import com.mrbeans.circulosestudiobackend.common.dto.SuccessResponse;
import com.mrbeans.circulosestudiobackend.role.dtos.CreateRoleDto;
import com.mrbeans.circulosestudiobackend.role.dtos.ResponseRoleDto;
import com.mrbeans.circulosestudiobackend.role.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createRoles(@RequestBody CreateRoleDto createRoleDto) {
        roleService.createRole(createRoleDto);
        SuccessResponse<Void> response = new SuccessResponse<>(HttpStatus.CREATED.value(),"Rol creado correctamente", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<ResponseRoleDto>>> getAllRoles() {
        SuccessResponse<List<ResponseRoleDto>> response = new SuccessResponse<>(HttpStatus.OK.value(),"Roles obtenidos correctamente", roleService.findAll());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        SuccessResponse<Void> response = new SuccessResponse<>(HttpStatus.OK.value(),"Rol eliminado correctamente", null);
        return ResponseEntity.ok(response);
    }

}
