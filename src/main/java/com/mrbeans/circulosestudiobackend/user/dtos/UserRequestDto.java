package com.mrbeans.circulosestudiobackend.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UserRequestDto {

    @NotBlank(message = "El nombre no debe estar vacío")
    private String name;

    @Email(message = "El correo debe ser válido")
    @NotBlank(message = "El correo no debe estar vacío")
    private String email;

    @NotBlank(message = "El password no debe estar vacío")
    private String password;

    @NotNull(message = "La imagen no debe estar vacía")
    private UUID imageDocumentId;

    @NotBlank(message = "El nombre del rol no debe estar vacío")
    private String roleName;

}
