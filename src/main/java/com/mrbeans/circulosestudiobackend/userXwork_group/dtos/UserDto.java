package com.mrbeans.circulosestudiobackend.userXwork_group.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDto {
    @JsonProperty("_id")
    private UUID id;

    @JsonProperty("userXWorkgroupId")
    private UUID userXWorkgroupId;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("image")
    private String image;

    @JsonProperty("email")
    private String email;
}
