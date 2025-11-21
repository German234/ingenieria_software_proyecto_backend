package com.mrbeans.circulosestudiobackend.userXwork_group.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserCountResponseDto {
    @JsonProperty("usuarios")
    private List<UserResponseWorkGroupDto> usuarios;

    @JsonProperty("total")
    private Integer total;
}