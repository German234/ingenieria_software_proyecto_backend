package com.mrbeans.circulosestudiobackend.attendance.repository;


import com.mrbeans.circulosestudiobackend.attendance.entity.AttendanceEntity;
import com.mrbeans.circulosestudiobackend.userXwork_group.entitiy.UserXWorkGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAttendanceRepository extends JpaRepository<AttendanceEntity, UUID> {

    List<AttendanceEntity>
    findByUserXWorkGroup_WorkGroup_IdAndDateBetween(
            UUID workGroupId,
            LocalDate startDate,
            LocalDate endDate
    );
    Optional<AttendanceEntity> findByUserXWorkGroupAndDate(UserXWorkGroupEntity uxwg, LocalDate date);
    List<AttendanceEntity> findByUserXWorkGroup_WorkGroup_Id(UUID workGroupId);

}
