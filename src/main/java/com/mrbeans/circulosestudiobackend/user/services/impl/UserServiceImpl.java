package com.mrbeans.circulosestudiobackend.user.services.impl;

import com.mrbeans.circulosestudiobackend.common.exception.GenericException;
import com.mrbeans.circulosestudiobackend.documents.entity.DocumentEntity;
import com.mrbeans.circulosestudiobackend.documents.repositories.IDocumentRepository;
import com.mrbeans.circulosestudiobackend.role.entity.RoleEntity;
import com.mrbeans.circulosestudiobackend.role.services.impl.RoleServiceImpl;
import com.mrbeans.circulosestudiobackend.user.dtos.UserRequestDto;
import com.mrbeans.circulosestudiobackend.user.dtos.UserResponseDto;
import com.mrbeans.circulosestudiobackend.user.dtos.UserUpdateProfileDto;
import com.mrbeans.circulosestudiobackend.user.entity.UserEntity;
import com.mrbeans.circulosestudiobackend.user.repositories.IUserRepository;
import com.mrbeans.circulosestudiobackend.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private IDocumentRepository documentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponseDto> findAll() {
        List<UserResponseDto> users = userRepository.findAll().stream()
                .map(this::toDto)
                .toList();

        if (users.isEmpty()) {
            throw new GenericException("Usuarios no encontrados");
        }
        return users;
    }

    @Override
    public UserResponseDto findById(UUID id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new GenericException("Usuario no encontrado"));
        return toDto(user);
    }

    @Override
    public List<UserResponseDto> getAllStudents() {
        List<UserResponseDto> userRoles = userRepository.findByRoleName("ALUMNO").stream()
                .map(this::toDto)
                .toList();
        if (userRoles.isEmpty()) {
            throw new GenericException("Ningun usuario encontrado para el rol de alumno");
        }
        return userRoles;
    }

    @Override
    public List<UserResponseDto> getAllTutors() {
        List<UserResponseDto> userRoles = userRepository.findByRoleName("TUTOR").stream()
                .map(this::toDto)
                .toList();
        if (userRoles.isEmpty()) {
            throw new GenericException("Ningun usuario encontrado para el rol de tutor");
        }
        return userRoles;
    }

    @Override
    public void createUser(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new GenericException("El correo electrónico ya existe");
        }

        UserEntity user = new UserEntity();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        RoleEntity rol = roleService.findByName(dto.getRoleName().toUpperCase());

        user.setRole(rol);

        DocumentEntity imgDoc = documentRepository.findById(dto.getImageDocumentId()).orElseThrow(() -> new GenericException("Imagen no encontrada"));
        user.setImageDocument(imgDoc);

        userRepository.save(user);
    }

    @Override
    public void updateUserProfile(UserUpdateProfileDto dto, UUID id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new GenericException("Usuario no encontrado"));
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        if (dto.getImageDocumentId() != null) {
            DocumentEntity imgDoc = documentRepository.findById(dto.getImageDocumentId()).orElseThrow(() -> new GenericException("Image not found"));
            user.setImageDocument(imgDoc);
        }

        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if(!user.isActive()){
            user.setActive(true);
        }

        userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new GenericException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    private UserResponseDto toDto(UserEntity user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setNombre(user.getName());
        dto.setEmail(user.getEmail());
        dto.setDocumentId(user.getImageDocument().getId());
        dto.setImageUrl(user.getImageDocument().getUrl());
        dto.setRoleName(user.getRole().getName());
        dto.setActive(user.isActive());
        return dto;
    }
}
