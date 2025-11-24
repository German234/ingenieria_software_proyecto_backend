package com.mrbeans.circulosestudiobackend.user.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mrbeans.circulosestudiobackend.common.dto.SuccessResponse;
import com.mrbeans.circulosestudiobackend.security.CustomUserPrincipal;
import com.mrbeans.circulosestudiobackend.user.dtos.UpdatePasswordDto;
import com.mrbeans.circulosestudiobackend.user.dtos.UserRequestDto;
import com.mrbeans.circulosestudiobackend.user.dtos.UserResponseDto;
import com.mrbeans.circulosestudiobackend.user.dtos.UserStatisticsResponseDto;
import com.mrbeans.circulosestudiobackend.user.dtos.UserUpdateProfileDto;
import com.mrbeans.circulosestudiobackend.user.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<SuccessResponse<List<UserResponseDto>>> getUsers() {
        List<UserResponseDto> users = userService.findAll();
        SuccessResponse<List<UserResponseDto>> resp
                = new SuccessResponse<>(HttpStatus.OK.value(), "Usuarios obtenidos correctamente", users);
        return ResponseEntity.ok(resp);
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
//    @GetMapping("/role/alumno")
//    public ResponseEntity<SuccessResponse<List<UserResponseDto>>> getAllStudents() {
//        List<UserResponseDto> users = userService.getAllStudents();
//        SuccessResponse<List<UserResponseDto>> resp =
//                new SuccessResponse<>(HttpStatus.OK.value(),"Alumnos obtenidos correctamente", users);
//        return ResponseEntity.ok(resp);
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/role/tutor")
//    public ResponseEntity<SuccessResponse<List<UserResponseDto>>> getAllTutors() {
//        List<UserResponseDto> users = userService.getAllTutors();
//        SuccessResponse<List<UserResponseDto>> resp =
//                new SuccessResponse<>(HttpStatus.OK.value(),"Tutores obtenidos correctamente", users);
//        return ResponseEntity.ok(resp);
//    }
//    @GetMapping("/{id}")
//    public ResponseEntity<SuccessResponse<UserResponseDto>> getUserById(
//            @PathVariable UUID id) {
//        UserResponseDto user = userService.findById(id);
//        SuccessResponse<UserResponseDto> resp =
//                new SuccessResponse<>(HttpStatus.OK.value(),"Usuario encontrado", user);
//        return ResponseEntity.ok(resp);
//    }
    @PostMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<SuccessResponse<Void>> createUser(
            @Valid @RequestBody UserRequestDto dto) {
        userService.createUser(dto);
        SuccessResponse<Void> resp
                = new SuccessResponse<>(HttpStatus.CREATED.value(), "Usuario creado exitosamente", null);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resp);
    }

    @PutMapping("/profile/me")
    public ResponseEntity<SuccessResponse<Void>> updateUserProfile(
            @Valid @RequestBody UserUpdateProfileDto dto,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        UUID userId = principal.getId();
        userService.updateUserProfile(dto, userId);

        SuccessResponse<Void> resp = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Usuario actualizado correctamente",
                null
        );
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<SuccessResponse<Void>> updatePassword(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePasswordDto updatePasswordDto,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        UUID authenticatedUserId = principal.getId();
        userService.updatePassword(updatePasswordDto, id, authenticatedUserId);

        SuccessResponse<Void> resp = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Contraseña actualizada exitosamente",
                null
        );
        return ResponseEntity.ok(resp);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteUser(
            @PathVariable UUID id, @AuthenticationPrincipal CustomUserPrincipal principal) {
        if (id.equals(principal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse<>(HttpStatus.FORBIDDEN.value(), "No puedes eliminar tu propio usuario", null));
        }
        userService.deleteUser(id);
        SuccessResponse<Void> resp
                = new SuccessResponse<>(HttpStatus.OK.value(), "Usuario eliminado correctamente", null);
        return ResponseEntity.ok(resp);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/statistics")
    public ResponseEntity<SuccessResponse<UserStatisticsResponseDto>> getUserStatistics(
            @RequestParam(required = false) String lastActivityFromDate,
            @RequestParam(required = false) String lastActivityToDate
    ) {
        LocalDate fromDate = null;
        LocalDate toDate = null;

        if (lastActivityFromDate != null) {
            fromDate = LocalDate.parse(lastActivityFromDate);
        }
        if (lastActivityToDate != null) {
            toDate = LocalDate.parse(lastActivityToDate);
        }

        UserStatisticsResponseDto statistics = userService.getUserStatistics(fromDate, toDate);
        SuccessResponse<UserStatisticsResponseDto> resp = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Estadísticas de usuarios obtenidas correctamente",
                statistics
        );
        return ResponseEntity.ok(resp);
    }
}
