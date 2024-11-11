package com.bookmytour.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "El campo email no puede estar vacío")
    @Email(message = "Formato de email no válido")
    private String email;

    @NotBlank(message = "El campo password no puede estar vacío")
    private String password;
}