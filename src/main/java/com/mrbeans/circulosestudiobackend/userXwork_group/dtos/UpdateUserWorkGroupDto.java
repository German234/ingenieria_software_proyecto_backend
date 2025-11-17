package com.mrbeans.circulosestudiobackend.userXwork_group.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateUserWorkGroupDto {
    @JsonProperty("name")
    private String name;

    private UUID backgroundImageId;

    private List<UUID> userIds;

}
