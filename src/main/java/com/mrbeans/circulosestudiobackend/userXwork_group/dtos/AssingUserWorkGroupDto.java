package com.mrbeans.circulosestudiobackend.userXwork_group.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AssingUserWorkGroupDto {

    @NotNull(message = "El workGroupId no puede ser nulo")
    private UUID workGroupId;

    @NotEmpty(message = "La lista de userIds no puede estar vacía")
    private List<UUID> userIds;

}
