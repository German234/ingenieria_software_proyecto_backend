package com.mrbeans.circulosestudiobackend.userXwork_group.controller;

import com.mrbeans.circulosestudiobackend.common.dto.SuccessResponse;
import com.mrbeans.circulosestudiobackend.security.CustomUserPrincipal;
import com.mrbeans.circulosestudiobackend.userXwork_group.dtos.*;
import com.mrbeans.circulosestudiobackend.userXwork_group.service.UserXWorkGroupService;
import com.mrbeans.circulosestudiobackend.work_group.dtos.WorkGroupResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-x-work-groups")
public class UserXWorkGroupController {

    @Autowired
    private UserXWorkGroupService userXWorkGroupService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<Void>> createUserXWorkGroup(
            @RequestBody CreateUserWorkGroupDto createDto
    ) {
        userXWorkGroupService.createUserWorkGroup(createDto);

        SuccessResponse<Void> response = new SuccessResponse<>(
                HttpStatus.CREATED.value(),
                "Work group creado satisfactoriamente",
                null
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/workgroup/{workGroupId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<SuccessResponse<Void>> updateUserXWorkGroup(@PathVariable UUID workGroupId, @RequestBody UpdateUserWorkGroupDto updateDto) {
        userXWorkGroupService.updateUserWorkGroup(workGroupId, updateDto);

        SuccessResponse<Void> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Grupo de trabajo actualizado satisfactoriamente",
                null
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<List<WorkGroupResponseDto>>> getUserXWorkGroups(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        UUID userId = principal.getId();
        List<WorkGroupResponseDto> grupos =
                userXWorkGroupService.getWorkGroupsByUserId(userId);

        SuccessResponse<List<WorkGroupResponseDto>> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Grupos de trabajo del usuario encontrados",
                grupos
        );
        return ResponseEntity.ok(response);
    }
//
//    @GetMapping("/work-group/{workGroupId}")
//    public ResponseEntity<SuccessResponse<List<UserResponseWorkGroupDto>>> getUsersByWorkGroup(
//            @PathVariable UUID workGroupId
//    ) {
//        List<UserResponseWorkGroupDto> usuarios =
//                userXWorkGroupService.getUsersByWorkGroupId(workGroupId);
//
//        SuccessResponse<List<UserResponseWorkGroupDto>> response = new SuccessResponse<>(
//                HttpStatus.OK.value(),
//                "Usuarios encontrados en el grupo de trabajo",
//                usuarios
//        );
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/work-group/{workGroupId}/alumnos")
//    public ResponseEntity<SuccessResponse<List<UserResponseWorkGroupDto>>> getStudentsByWorkGroup(
//            @PathVariable UUID workGroupId
//    ) {
//        List<UserResponseWorkGroupDto> alumnos =
//                userXWorkGroupService.getAllStudentsByWorkGroupId(workGroupId);
//
//        SuccessResponse<List<UserResponseWorkGroupDto>> response = new SuccessResponse<>(
//                HttpStatus.OK.value(),
//                "Estudiantes encontrados en el grupo de trabajo",
//                alumnos
//        );
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/work-group/{workGroupId}/tutores")
//    public ResponseEntity<SuccessResponse<List<UserResponseWorkGroupDto>>> getTutorsByWorkGroup(
//            @PathVariable UUID workGroupId
//    ) {
//        List<UserResponseWorkGroupDto> tutores =
//                userXWorkGroupService.getAllTutorsByWorkGroupId(workGroupId);
//
//        SuccessResponse<List<UserResponseWorkGroupDto>> response = new SuccessResponse<>(
//                HttpStatus.OK.value(),
//                "Tutores encontrados en el grupo de trabajo",
//                tutores
//        );
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/alumnos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<UserResponseWorkGroupDto>>> getAllStudentsWithWorkgroups() {
        List<UserResponseWorkGroupDto> alumnos =
                userXWorkGroupService.getAllAlumnosWithWorkgroups();

        SuccessResponse<List<UserResponseWorkGroupDto>> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Todos los alumnos con grupos de trabajo obtenidos",
                alumnos
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tutores")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<UserResponseWorkGroupDto>>> getAllTutorsWithWorkgroups() {
        List<UserResponseWorkGroupDto> tutores =
                userXWorkGroupService.getAllTutorsWithWorkgroups();

        SuccessResponse<List<UserResponseWorkGroupDto>> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Todos los tutores con grupos de trabajo obtenidos",
                tutores
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/work-group/slug/{slug}")
    public ResponseEntity<SuccessResponse<ResponseWorkGroupDto>> getByWorkGroupSlug(
            @PathVariable String slug
    ) {
        ResponseWorkGroupDto response = userXWorkGroupService.getByWorkGroupSlug(slug);
        SuccessResponse<ResponseWorkGroupDto> successResponse = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Grupo de trabajo encontrado",
                response
        );
        return ResponseEntity.ok(successResponse);
    }
}
