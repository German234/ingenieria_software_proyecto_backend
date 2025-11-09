package com.mrbeans.circulosestudiobackend.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class SuccessResponse<T> {
    private LocalDateTime timestamp;
    private int statusCode;
    private String message;
    private T data;

    public SuccessResponse(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message    = message;
        this.data       = data;
        this.timestamp  = LocalDateTime.now();
    }

    public SuccessResponse(String message, T data) {
        this.timestamp = LocalDateTime.now();
        this.message   = message;
        this.data      = data;
    }
}

