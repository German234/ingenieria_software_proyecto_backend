package com.mrbeans.circulosestudiobackend.userXwork_group.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mrbeans.circulosestudiobackend.common.dto.PaginationResponse;
import com.mrbeans.circulosestudiobackend.common.dto.SuccessResponse;
import com.mrbeans.circulosestudiobackend.security.CustomUserPrincipal;
import com.mrbeans.circulosestudiobackend.userXwork_group.dtos.CourseStatisticsResponseDto;
import com.mrbeans.circulosestudiobackend.userXwork_group.dtos.CourseSummaryDto;
import com.mrbeans.circulosestudiobackend.userXwork_group.dtos.CourseWithStatisticsDto;
import com.mrbeans.circulosestudiobackend.userXwork_group.dtos.CreateUserWorkGroupDto;
import com.mrbeans.circulosestudiobackend.userXwork_group.dtos.ResponseWorkGroupDto;
import com.mrbeans.circulosestudiobackend.userXwork_group.dtos.UpdateUserWorkGroupDto;
import com.mrbeans.circulosestudiobackend.userXwork_group.dtos.UserCountResponseDto;
import com.mrbeans.circulosestudiobackend.userXwork_group.dtos.UserResponseWorkGroupDto;
import com.mrbeans.circulosestudiobackend.userXwork_group.service.UserXWorkGroupService;
import com.mrbeans.circulosestudiobackend.work_group.dtos.WorkGroupResponseDto;

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
        List<WorkGroupResponseDto> grupos
                = userXWorkGroupService.getWorkGroupsByUserId(userId);

        SuccessResponse<List<WorkGroupResponseDto>> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Grupos de trabajo del usuario encontrados",
                grupos
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/alumnos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<UserCountResponseDto>> getAllStudentsWithWorkgroups(
            @RequestParam(required = false) UUID workGroupId
    ) {
        UserCountResponseDto alumnosWithCount = userXWorkGroupService.getAllStudentsWithCount(workGroupId);

        SuccessResponse<UserCountResponseDto> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Alumnos obtenidos",
                alumnosWithCount
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/alumnos/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<PaginationResponse<UserResponseWorkGroupDto>>> getAlumnosWithPagination(
            @RequestParam(required = false) UUID workGroupId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size);
        PaginationResponse<UserResponseWorkGroupDto> alumnos
                = userXWorkGroupService.getAlumnosWithPagination(workGroupId, pageable);

        SuccessResponse<PaginationResponse<UserResponseWorkGroupDto>> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Alumnos obtenidos con paginación",
                alumnos
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tutores")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<UserCountResponseDto>> getAllTutorsWithWorkgroups(
            @RequestParam(required = false) UUID workGroupId
    ) {
        UserCountResponseDto tutoresWithCount = userXWorkGroupService.getAllTutorsWithCount(workGroupId);

        SuccessResponse<UserCountResponseDto> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Tutores obtenidos",
                tutoresWithCount
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tutores/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<PaginationResponse<UserResponseWorkGroupDto>>> getTutorsWithPagination(
            @RequestParam(required = false) UUID workGroupId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size);
        PaginationResponse<UserResponseWorkGroupDto> tutores
                = userXWorkGroupService.getTutorsWithPagination(workGroupId, pageable);

        SuccessResponse<PaginationResponse<UserResponseWorkGroupDto>> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Tutores obtenidos con paginación",
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/courses/summary")
    public ResponseEntity<SuccessResponse<List<CourseSummaryDto>>> getAllCoursesWithUserCounts() {
        List<CourseSummaryDto> courses = userXWorkGroupService.getAllCoursesWithUserCounts();

        SuccessResponse<List<CourseSummaryDto>> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Resumen de todos los cursos con cantidad de alumnos y tutores",
                courses
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/courses/statistics")
    public ResponseEntity<SuccessResponse<CourseStatisticsResponseDto>> getCoursesWithStatistics(
            @RequestParam(required = false) String status
    ) {
        com.mrbeans.circulosestudiobackend.work_group.enums.CourseStatus courseStatus = null;
        if (status != null) {
            try {
                courseStatus = com.mrbeans.circulosestudiobackend.work_group.enums.CourseStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new com.mrbeans.circulosestudiobackend.common.exception.GenericException("Estado inválido. Los valores válidos son: ACTIVE, PAUSED, FINISHED");
            }
        }

        List<CourseWithStatisticsDto> courses = userXWorkGroupService.getCoursesWithStatistics(courseStatus);
        Long totalActive = userXWorkGroupService.getTotalCoursesByStatus(com.mrbeans.circulosestudiobackend.work_group.enums.CourseStatus.ACTIVE);

        // If a specific status is requested, count courses by that status, otherwise count all active courses
        Long totalCoursesByStatus = (courseStatus != null)
                ? userXWorkGroupService.getTotalCoursesByStatus(courseStatus) : totalActive;

        CourseStatisticsResponseDto response = new CourseStatisticsResponseDto();
        response.setTotalActiveCourses(totalCoursesByStatus);
        response.setCourses(courses);

        SuccessResponse<CourseStatisticsResponseDto> successResponse = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Estadísticas de cursos obtenidas exitosamente",
                response
        );
        return ResponseEntity.ok(successResponse);
    }
}
