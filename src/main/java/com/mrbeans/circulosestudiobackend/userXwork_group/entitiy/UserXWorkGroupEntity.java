package com.mrbeans.circulosestudiobackend.userXwork_group.entitiy;

import com.mrbeans.circulosestudiobackend.attendance.entity.AttendanceEntity;
import com.mrbeans.circulosestudiobackend.user.entity.UserEntity;
import com.mrbeans.circulosestudiobackend.work_group.entity.WorkGroupEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_x_work_group")
@Data
public class UserXWorkGroupEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workgroup", nullable = false)
    private WorkGroupEntity workGroup;

    @OneToMany(
            mappedBy = "userXWorkGroup",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AttendanceEntity> attendances = new ArrayList<>();

}
