package com.bookmytour.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String firstName;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String lastName;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Formato de email no válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
}