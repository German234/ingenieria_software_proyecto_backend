package com.mrbeans.circulosestudiobackend.attendance.service.Impl;

import com.mrbeans.circulosestudiobackend.attendance.dtos.CreateAttendanceDto;
import com.mrbeans.circulosestudiobackend.attendance.dtos.InternalStructureAttendanceDto;
import com.mrbeans.circulosestudiobackend.attendance.dtos.ResponseAttendance;
import com.mrbeans.circulosestudiobackend.attendance.entity.AttendanceEntity;
import com.mrbeans.circulosestudiobackend.attendance.repository.IAttendanceRepository;
import com.mrbeans.circulosestudiobackend.attendance.service.AttendanceService;
import com.mrbeans.circulosestudiobackend.common.exception.GenericException;
import com.mrbeans.circulosestudiobackend.userXwork_group.entitiy.UserXWorkGroupEntity;
import com.mrbeans.circulosestudiobackend.userXwork_group.repositories.IUserXWorkGroupRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private IAttendanceRepository attendanceRepository;

    @Autowired
    private IUserXWorkGroupRepository userXWorkGroupRepository;

    @Override
    @Transactional
    public void createAttendance(CreateAttendanceDto createAttendanceDto) {
        for (InternalStructureAttendanceDto dto : createAttendanceDto.getAsistencias()) {
            UserXWorkGroupEntity uxwg = userXWorkGroupRepository.findById(dto.getUserXWorkGroupId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "No existe UserXWorkGroup con id: " + dto.getUserXWorkGroupId()));

            Optional<AttendanceEntity> opt = attendanceRepository
                    .findByUserXWorkGroupAndDate(uxwg, dto.getDate());

            if (opt.isPresent()) {
                AttendanceEntity existing = opt.get();
                existing.setStatus(dto.getStatus());
                attendanceRepository.save(existing);
            } else {
                AttendanceEntity attendance = new AttendanceEntity();
                attendance.setUserXWorkGroup(uxwg);
                attendance.setDate(dto.getDate());
                attendance.setStatus(dto.getStatus());
                attendanceRepository.save(attendance);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<ResponseAttendance>>
    getAttendanceByMonthAndYearAndWorkGroupId(UUID workGroupId, int year, int month) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end   = start.withDayOfMonth(start.lengthOfMonth());

        List<AttendanceEntity> records = attendanceRepository
                .findByUserXWorkGroup_WorkGroup_IdAndDateBetween(workGroupId, start, end);

        return records.stream()
                .map(record -> {
                    ResponseAttendance dto = new ResponseAttendance();
                    dto.setId(record.getId());
                    dto.setAlumnoId(record.getUserXWorkGroup().getUser().getId());
                    dto.setFecha(record.getDate());
                    dto.setEstado(record.getStatus());
                    dto.setNombre(record.getUserXWorkGroup().getUser().getName());
                    dto.setImagen(record.getUserXWorkGroup().getUser().getImageDocument().getUrl());
                    return dto;
                })
                .collect(Collectors.groupingBy(
                        dto -> dto.getFecha().toString()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseAttendance> getAllAttendanceByWorkGroupId(UUID workGroupId) {

        if (!userXWorkGroupRepository.existsByWorkGroupId(workGroupId)) {
            throw new GenericException("WorkGroup no encontrado: " + workGroupId);
        }

        List<AttendanceEntity> records =
                attendanceRepository.findByUserXWorkGroup_WorkGroup_Id(workGroupId);

        return records.stream().map(record -> {
            ResponseAttendance dto = new ResponseAttendance();
            dto.setId(record.getId());
            dto.setAlumnoId(record.getUserXWorkGroup().getUser().getId());
            dto.setFecha(record.getDate());
            dto.setEstado(record.getStatus());
            dto.setNombre(record.getUserXWorkGroup().getUser().getName());
            dto.setImagen(record.getUserXWorkGroup().getUser().getImageDocument().getUrl());
            return dto;
        }).toList();
    }
}
