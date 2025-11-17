package com.mrbeans.circulosestudiobackend.attendance.entity;

import com.mrbeans.circulosestudiobackend.userXwork_group.entitiy.UserXWorkGroupEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "attendance")
@Data
public class AttendanceEntity {
    @Id
    @UuidGenerator
    @Column
    private UUID id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "status", nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_x_work_group_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_attendance_userxwg")
    )
    private UserXWorkGroupEntity userXWorkGroup;

}
