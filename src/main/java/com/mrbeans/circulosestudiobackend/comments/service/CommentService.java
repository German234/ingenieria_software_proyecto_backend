package com.mrbeans.circulosestudiobackend.comments.service;

import com.mrbeans.circulosestudiobackend.comments.dtos.CreateCommentDto;
import com.mrbeans.circulosestudiobackend.comments.dtos.ResponseCommentDto;
import com.mrbeans.circulosestudiobackend.comments.dtos.UpdateCommentDto;

import java.util.List;
import java.util.UUID;

public interface CommentService {

    ResponseCommentDto createComment(UUID userId,CreateCommentDto createCommentDto);

    ResponseCommentDto updateComment(UUID id, UpdateCommentDto updateCommentDto, UUID userId);

    void deleteComment(UUID id, UUID userId);

    ResponseCommentDto findCommentById(UUID id);

    List<ResponseCommentDto> findCommentsBySupportMaterialId(UUID supportMaterialId);
}
