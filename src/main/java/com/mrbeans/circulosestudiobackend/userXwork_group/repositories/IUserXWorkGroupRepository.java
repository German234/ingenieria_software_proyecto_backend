package com.mrbeans.circulosestudiobackend.userXwork_group.repositories;

import com.mrbeans.circulosestudiobackend.userXwork_group.entitiy.UserXWorkGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface IUserXWorkGroupRepository extends JpaRepository<UserXWorkGroupEntity, UUID> {
    boolean existsByUserIdAndWorkGroupId(UUID userId, UUID workGroupId);
    void deleteUserXworkGroupEntitiesByUserIdAndWorkGroupId(UUID userId, UUID workGroupId);
    List<UserXWorkGroupEntity> findAllByUserId(UUID userId);
    List<UserXWorkGroupEntity> findAllByWorkGroupId(UUID workGroupId);
    List<UserXWorkGroupEntity> findByWorkGroupId_AndUser_Role_Name(UUID workGroupId, String roleName);
    boolean existsByWorkGroupId(UUID workGroupId);
}
