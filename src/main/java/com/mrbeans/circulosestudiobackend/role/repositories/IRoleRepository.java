package com.mrbeans.circulosestudiobackend.role.repositories;


import com.mrbeans.circulosestudiobackend.role.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IRoleRepository extends JpaRepository<RoleEntity, UUID> {

    RoleEntity findByName(String name);
    boolean existsByName(String name);
}
