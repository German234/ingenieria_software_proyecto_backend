package com.mrbeans.circulosestudiobackend.userXwork_group.service;

import com.mrbeans.circulosestudiobackend.user.dtos.UserResponseDto;
import com.mrbeans.circulosestudiobackend.userXwork_group.dtos.*;
import com.mrbeans.circulosestudiobackend.work_group.dtos.WorkGroupResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserXWorkGroupService {

    void createUserWorkGroup(CreateUserWorkGroupDto dto);

    void assignUsersToWorkGroup(AssingUserWorkGroupDto dto);

    void deleteUserXWorkGroup(UUID userId, UUID workGroupId);

    List<WorkGroupResponseDto> getWorkGroupsByUserId(UUID userId);

    List<UserResponseWorkGroupDto> getUsersByWorkGroupId(UUID workGroupId);

    List<UserResponseWorkGroupDto> getAllStudentsByWorkGroupId(UUID workGroupId);

    List<UserResponseWorkGroupDto> getAllTutorsByWorkGroupId(UUID workGroupId);

    List<UserResponseWorkGroupDto> getAllAlumnosWithWorkgroups();

    List<UserResponseWorkGroupDto> getAllTutorsWithWorkgroups();

    ResponseWorkGroupDto getByWorkGroupSlug(String slug);

    void updateUserWorkGroup(UUID workGroupId, UpdateUserWorkGroupDto dto);

}
