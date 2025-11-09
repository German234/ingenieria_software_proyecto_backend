package com.mrbeans.circulosestudiobackend.user.services;

import com.mrbeans.circulosestudiobackend.user.dtos.UserRequestDto;
import com.mrbeans.circulosestudiobackend.user.dtos.UserResponseDto;
import com.mrbeans.circulosestudiobackend.user.dtos.UserUpdateProfileDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserResponseDto> findAll();
    void createUser(UserRequestDto userRequestDto);
    void updateUserProfile(UserUpdateProfileDto user, UUID id);
    void deleteUser(UUID id);
    UserResponseDto findById(UUID id);
    List<UserResponseDto> getAllStudents();
    List<UserResponseDto> getAllTutors();
}
