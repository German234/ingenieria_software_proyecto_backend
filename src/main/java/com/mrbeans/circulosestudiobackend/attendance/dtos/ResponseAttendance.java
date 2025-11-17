package com.mrbeans.circulosestudiobackend.attendance.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ResponseAttendance {
    private UUID id;
    private UUID alumnoId;
    private LocalDate fecha;
    private String estado;
    private String nombre;
    private String imagen;
}
