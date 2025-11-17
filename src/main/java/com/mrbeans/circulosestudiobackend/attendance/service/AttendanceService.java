package com.mrbeans.circulosestudiobackend.attendance.service;

import com.mrbeans.circulosestudiobackend.attendance.dtos.CreateAttendanceDto;
import com.mrbeans.circulosestudiobackend.attendance.dtos.ResponseAttendance;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AttendanceService {

    void createAttendance(CreateAttendanceDto createAttendanceDto);
    Map<String, List<ResponseAttendance>>
    getAttendanceByMonthAndYearAndWorkGroupId(UUID workGroupId, int year, int month);
    List<ResponseAttendance> getAllAttendanceByWorkGroupId(UUID workGroupId);
}
