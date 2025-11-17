package com.mrbeans.circulosestudiobackend.role.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class ResponseRoleDto {

    private UUID id;
    private String name;

}
