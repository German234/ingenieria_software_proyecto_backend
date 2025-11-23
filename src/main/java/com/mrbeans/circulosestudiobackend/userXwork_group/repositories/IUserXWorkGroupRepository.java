package com.mrbeans.circulosestudiobackend.userXwork_group.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mrbeans.circulosestudiobackend.userXwork_group.entitiy.UserXWorkGroupEntity;

public interface IUserXWorkGroupRepository extends JpaRepository<UserXWorkGroupEntity, UUID> {

    boolean existsByUserIdAndWorkGroupId(UUID userId, UUID workGroupId);

    void deleteUserXworkGroupEntitiesByUserIdAndWorkGroupId(UUID userId, UUID workGroupId);

    List<UserXWorkGroupEntity> findAllByUserId(UUID userId);

    List<UserXWorkGroupEntity> findAllByWorkGroupId(UUID workGroupId);

    List<UserXWorkGroupEntity> findByWorkGroupId_AndUser_Role_Name(UUID workGroupId, String roleName);

    boolean existsByWorkGroupId(UUID workGroupId);

    @Query("SELECT u FROM UserXWorkGroupEntity u WHERE (:workGroupId IS NULL OR u.workGroup.id = :workGroupId) AND u.user.role.name = :roleName")
    Page<UserXWorkGroupEntity> findByWorkGroupIdAndRoleName(UUID workGroupId, String roleName, Pageable pageable);

    @Query("SELECT wg.id, wg.name, wg.slug, wg.imageDocument.url, "
            + "COUNT(CASE WHEN u.user.role.name = 'ALUMNO' THEN 1 END) as cantidadAlumnos, "
            + "COUNT(CASE WHEN u.user.role.name = 'TUTOR' THEN 1 END) as cantidadTutores "
            + "FROM WorkGroupEntity wg "
            + "LEFT JOIN wg.userLinks u "
            + "GROUP BY wg.id, wg.name, wg.slug, wg.imageDocument.url")
    List<Object[]> findAllWorkGroupsWithUserCounts();

    @Query("SELECT wg.id, wg.name, wg.slug, wg.imageDocument.url, wg.status, "
            + "COUNT(u.user.id) as cantidadInscripciones "
            + "FROM WorkGroupEntity wg "
            + "LEFT JOIN wg.userLinks u "
            + "WHERE (:status IS NULL OR wg.status = :status) "
            + "GROUP BY wg.id, wg.name, wg.slug, wg.imageDocument.url, wg.status")
    List<Object[]> findCoursesWithStatisticsByStatus(com.mrbeans.circulosestudiobackend.work_group.enums.CourseStatus status);

    @Query("SELECT COUNT(DISTINCT wg.id) "
            + "FROM WorkGroupEntity wg "
            + "WHERE wg.status = :status")
    Long countCoursesByStatus(com.mrbeans.circulosestudiobackend.work_group.enums.CourseStatus status);

    @Query("SELECT COUNT(DISTINCT u.user.id) FROM UserXWorkGroupEntity u WHERE u.user.role.name = 'ALUMNO'")
    Long countAllStudents();

    @Query("SELECT COUNT(DISTINCT u.user.id) FROM UserXWorkGroupEntity u WHERE u.user.role.name = 'TUTOR'")
    Long countAllTutors();

    @Query("SELECT COUNT(DISTINCT u.user.id) FROM UserXWorkGroupEntity u WHERE u.workGroup.id = :workGroupId AND u.user.role.name = 'ALUMNO'")
    Long countStudentsByWorkGroupId(UUID workGroupId);

    @Query("SELECT COUNT(DISTINCT u.user.id) FROM UserXWorkGroupEntity u WHERE u.workGroup.id = :workGroupId AND u.user.role.name = 'TUTOR'")
    Long countTutorsByWorkGroupId(UUID workGroupId);
}
