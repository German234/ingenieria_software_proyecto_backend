package com.mrbeans.circulosestudiobackend.comments.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ResponseCommentDto {

    private UUID id;

    private String message;

    private String userName;

    private String userEmail;

    private String userProfilePicture;

    private LocalDateTime createdAt;
}
