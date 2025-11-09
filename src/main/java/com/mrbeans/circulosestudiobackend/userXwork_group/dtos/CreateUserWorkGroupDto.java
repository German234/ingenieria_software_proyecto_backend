package com.mrbeans.circulosestudiobackend.userXwork_group.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateUserWorkGroupDto {
    @NotBlank(message = "El nombre no debe estar vacío")
    @JsonProperty("workGroupName")
    private String name;

    @NotBlank(message = "La imagen de fondo no debe estar vacía")
    private UUID backgroundImageId;

    @NotEmpty(message = "La lista de userIds no puede estar vacía")
    private List<UUID> userIds;

}
