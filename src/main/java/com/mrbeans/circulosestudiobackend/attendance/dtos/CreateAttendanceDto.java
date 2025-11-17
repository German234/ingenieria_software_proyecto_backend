package com.mrbeans.circulosestudiobackend.attendance.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CreateAttendanceDto {
    private List<InternalStructureAttendanceDto> asistencias;
}
