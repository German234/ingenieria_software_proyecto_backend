package com.mrbeans.circulosestudiobackend.userXwork_group.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserResponseWorkGroupDto {
    @JsonProperty("_id")
    private String id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("email")
    private String email;

    @JsonProperty("image")
    private String image;

    @JsonProperty("isActive")
    private boolean isActive;

    @JsonProperty("workgroups")
    private List<String> workgroups;
}