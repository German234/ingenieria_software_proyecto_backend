package com.mrbeans.circulosestudiobackend.work_group.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class WorkGroupUpdateDto {

    private String name;

    private UUID backgroundDocumentId;

    private String slug;

}
