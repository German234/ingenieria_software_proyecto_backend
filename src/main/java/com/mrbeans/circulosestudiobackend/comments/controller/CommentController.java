package com.mrbeans.circulosestudiobackend.comments.controller;

import com.mrbeans.circulosestudiobackend.comments.dtos.CreateCommentDto;
import com.mrbeans.circulosestudiobackend.comments.dtos.ResponseCommentDto;
import com.mrbeans.circulosestudiobackend.comments.dtos.UpdateCommentDto;
import com.mrbeans.circulosestudiobackend.comments.service.CommentService;
import com.mrbeans.circulosestudiobackend.common.dto.SuccessResponse;
import com.mrbeans.circulosestudiobackend.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<SuccessResponse<ResponseCommentDto>> createComment(
            @Valid @RequestBody CreateCommentDto createCommentDto,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        UUID userId = principal.getId();
        ResponseCommentDto comment = commentService.createComment(userId, createCommentDto);
        SuccessResponse<ResponseCommentDto> response = new SuccessResponse<>(
                HttpStatus.CREATED.value(),
                "Comentario creado exitosamente",
                comment
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<ResponseCommentDto>> updateComment(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCommentDto updateCommentDto,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        UUID userId = principal.getId();
        ResponseCommentDto updatedComment = commentService.updateComment(id, updateCommentDto, userId);
        SuccessResponse<ResponseCommentDto> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Comentario actualizado exitosamente",
                updatedComment
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteComment(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        UUID userId = principal.getId();
        commentService.deleteComment(id, userId);
        SuccessResponse<Void> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Comentario eliminado exitosamente",
                null
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ResponseCommentDto>> getCommentById(@PathVariable UUID id) {
        ResponseCommentDto comment = commentService.findCommentById(id);
        SuccessResponse<ResponseCommentDto> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Comentario encontrado",
                comment
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/support-material/{supportMaterialId}")
    public ResponseEntity<SuccessResponse<List<ResponseCommentDto>>> getCommentsBySupportMaterialId(
            @PathVariable UUID supportMaterialId) {
        List<ResponseCommentDto> comments = commentService.findCommentsBySupportMaterialId(supportMaterialId);
        SuccessResponse<List<ResponseCommentDto>> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Comentarios encontrados para el material de soporte",
                comments
        );
        return ResponseEntity.ok(response);
    }
}
