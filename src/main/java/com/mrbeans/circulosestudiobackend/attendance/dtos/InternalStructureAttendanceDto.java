package com.mrbeans.circulosestudiobackend.attendance.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class InternalStructureAttendanceDto {

    private UUID userXWorkGroupId;

    @JsonProperty("fecha")
    private LocalDate date;

    @JsonProperty("estado")
    private String status;

}
