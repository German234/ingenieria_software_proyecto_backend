package com.mrbeans.circulosestudiobackend.work_group.repositories;


import com.mrbeans.circulosestudiobackend.work_group.entity.WorkGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IWorkGroupsRepository extends JpaRepository<WorkGroupEntity, UUID> {
    boolean existsBySlug(String slug);
    boolean existsByName(String name);
    WorkGroupEntity findBySlug(String slug);
    boolean existsByNameAndIdNot(String name, UUID id);
    boolean existsBySlugAndIdNot(String slug, UUID id);
}
