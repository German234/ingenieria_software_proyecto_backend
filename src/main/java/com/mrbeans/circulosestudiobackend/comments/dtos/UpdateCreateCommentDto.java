package com.mrbeans.circulosestudiobackend.comments.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCreateCommentDto {

    @NotEmpty
    private String message;
}
