package com.mrbeans.circulosestudiobackend.user.dtos;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.UUID;

@Data
public class UserUpdateProfileDto {

    private String name;
    private UUID imageDocumentId;
    private String password;
}
