package com.mrbeans.circulosestudiobackend.user.repositories;

import com.mrbeans.circulosestudiobackend.user.entity.UserEntity;

import feign.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<UserEntity, UUID> {

    List<UserEntity> findByRoleName(String roleName);

    boolean existsByEmail(String email);

    UserEntity findByEmail(String email);

    Page<UserEntity> findByRoleName(String roleName, Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.isActive = true AND u.role.name = :roleName")
    List<UserEntity> findActiveByRoleName(String roleName);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.isActive = true AND u.role.name = :roleName")
    Long countActiveByRoleName(String roleName);

    @Query("SELECT u FROM UserEntity u WHERE u.isActive = true AND u.role.name = 'TUTOR'")
    List<UserEntity> findActiveTutors();

    @Query("SELECT u FROM UserEntity u WHERE u.isActive = true AND u.role.name = 'ALUMNO'")
    List<UserEntity> findActiveStudents();

    @Query("SELECT u FROM UserEntity u WHERE u.isActive = true AND u.role.name = 'ADMIN'")
    List<UserEntity> findActiveAdministrators();

    @Query("SELECT u FROM UserEntity u WHERE u.isActive = true AND u.role.name = :roleName "
            + "AND (:fromDate IS NULL OR EXISTS (SELECT 1 FROM UserXWorkGroupEntity uxwg JOIN uxwg.attendances a "
            + "WHERE uxwg.user = u AND a.date >= :fromDate)) "
            + "AND (:toDate IS NULL OR EXISTS (SELECT 1 FROM UserXWorkGroupEntity uxwg JOIN uxwg.attendances a "
            + "WHERE uxwg.user = u AND a.date <= :toDate))")
    List<UserEntity> findActiveByRoleNameWithActivityFilter(String roleName, LocalDate fromDate, LocalDate toDate);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.createdAt >= :firstDayOfMonth AND u.createdAt <= :lastDayOfMonth")
    Long countUsersCreatedInCurrentMonth(@Param("firstDayOfMonth") LocalDate firstDayOfMonth, @Param("lastDayOfMonth") LocalDate lastDayOfMonth);
}
