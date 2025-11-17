package com.mrbeans.circulosestudiobackend.comments.service.impl;

import com.mrbeans.circulosestudiobackend.comments.dtos.CreateCommentDto;
import com.mrbeans.circulosestudiobackend.comments.dtos.ResponseCommentDto;
import com.mrbeans.circulosestudiobackend.comments.dtos.UpdateCommentDto;
import com.mrbeans.circulosestudiobackend.comments.entity.CommentsEntity;
import com.mrbeans.circulosestudiobackend.comments.repository.ICommentRepository;
import com.mrbeans.circulosestudiobackend.comments.service.CommentService;
import com.mrbeans.circulosestudiobackend.common.exception.GenericException;
import com.mrbeans.circulosestudiobackend.support_material.entity.SupportMaterialEntity;
import com.mrbeans.circulosestudiobackend.support_material.repository.ISupportMaterialRepository;
import com.mrbeans.circulosestudiobackend.user.entity.UserEntity;
import com.mrbeans.circulosestudiobackend.user.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private ICommentRepository commentRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ISupportMaterialRepository supportMaterialRepository;

    @Override
    @Transactional
    public ResponseCommentDto createComment(UUID userId, CreateCommentDto createCommentDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new GenericException("Usuario no encontrado"));

        SupportMaterialEntity supportMaterial = supportMaterialRepository.findById(createCommentDto.getSupportMaterialId())
                .orElseThrow(() -> new GenericException("El material de soporte no existe"));

        CommentsEntity comment = new CommentsEntity();
        comment.setMessage(createCommentDto.getMessage());
        comment.setUser(user);
        comment.setSupportMaterial(supportMaterial);

        CommentsEntity savedComment = commentRepository.save(comment);
        return mapEntityToDto(savedComment);
    }

    @Override
    @Transactional
    public ResponseCommentDto updateComment(UUID id, UpdateCommentDto updateCommentDto, UUID userId) {
        CommentsEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new GenericException("Comentario no encontrado"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new GenericException("Usuario no encontrado"));

        if ( user.getRole().getName().equals("ALUMNO") || !comment.getUser().getId().equals(user.getId())) {
            throw new GenericException("No tienes permisos para actualizar este comentario");
        }

        comment.setMessage(updateCommentDto.getMessage());

        CommentsEntity updatedComment = commentRepository.save(comment);
        return mapEntityToDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(UUID id, UUID userId) {

        CommentsEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new GenericException("Comentario no encontrado"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new GenericException("Usuario no encontrado"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new GenericException("No tienes permisos para eliminar este comentario");
        }

        commentRepository.delete(comment);
    }

    @Override
    public ResponseCommentDto findCommentById(UUID id) {
        CommentsEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new GenericException("Comentario no encontrado"));
        return mapEntityToDto(comment);
    }

    @Override
    public List<ResponseCommentDto> findCommentsBySupportMaterialId(UUID supportMaterialId) {
        if (!supportMaterialRepository.existsById(supportMaterialId)) {
            throw new GenericException("El material de soporte no existe");
        }

        List<CommentsEntity> comments = commentRepository.findBySupportMaterialIdOrderByCreatedAtDesc(supportMaterialId);
        if (comments.isEmpty()) {
            throw new GenericException("No hay comentarios para este material de soporte");
        }

        return comments.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    private ResponseCommentDto mapEntityToDto(CommentsEntity entity) {
        ResponseCommentDto dto = new ResponseCommentDto();
        dto.setId(entity.getId());
        dto.setMessage(entity.getMessage());
        dto.setUserName(entity.getUser().getName());
        dto.setUserEmail(entity.getUser().getEmail());
        dto.setUserProfilePicture(entity.getUser().getImageDocument().getUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
