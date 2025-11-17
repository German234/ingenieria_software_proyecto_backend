package com.mrbeans.circulosestudiobackend.user.repositories;
import com.mrbeans.circulosestudiobackend.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<UserEntity, UUID> {

    List<UserEntity> findByRoleName(String roleName);
    boolean existsByEmail(String email);

    UserEntity findByEmail(String email);
    
    Page<UserEntity> findByRoleName(String roleName, Pageable pageable);
}
