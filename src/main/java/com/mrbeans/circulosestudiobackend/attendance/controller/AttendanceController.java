package com.mrbeans.circulosestudiobackend.attendance.controller;

import com.mrbeans.circulosestudiobackend.attendance.dtos.CreateAttendanceDto;
import com.mrbeans.circulosestudiobackend.attendance.dtos.ResponseAttendance;
import com.mrbeans.circulosestudiobackend.attendance.service.AttendanceService;
import com.mrbeans.circulosestudiobackend.common.dto.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<SuccessResponse<Void>> createAttendance(@RequestBody CreateAttendanceDto createAttendanceDto) {
        attendanceService.createAttendance(createAttendanceDto);

        SuccessResponse<Void> response = new SuccessResponse<>(
                HttpStatus.CREATED.value(),
                "Asistencias creadas o actualizadas satisfactoriamente",
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/work-group/{workGroupId}")
    public ResponseEntity<SuccessResponse<Map<String, List<ResponseAttendance>>>> getAttendanceByMonthAndYearAndWorkGroupId(
            @PathVariable UUID workGroupId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        var response = attendanceService.getAttendanceByMonthAndYearAndWorkGroupId(workGroupId, year, month);

        SuccessResponse<Map<String, List<ResponseAttendance>>> successResponse = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Asistencias obtenidas satisfactoriamente",
                response
        );
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/get-all-work-group/{workGroupId}")
    public ResponseEntity<SuccessResponse<List<ResponseAttendance>>> getAttendanceByWorkGroupId(
            @PathVariable UUID workGroupId
    ) {
        List<ResponseAttendance> attendanceList = attendanceService.getAllAttendanceByWorkGroupId(workGroupId);

        SuccessResponse<List<ResponseAttendance>> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Asistencias obtenidas satisfactoriamente",
                attendanceList
        );
        return ResponseEntity.ok(response);
    }

}
