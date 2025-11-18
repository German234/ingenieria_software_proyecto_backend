package com.mrbeans.circulosestudiobackend.keycloak.dtos;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class UserDto {

    private String id;

    private String code;

    private String username;

    private String firstName;

    private String lastName;

    private String institution;

    private String email;

    private String language;

    private Boolean isProfileComplete = false;

    private String password;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date updatedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date deletedAt;
}
