package com.bookmytour.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UsuarioDTO {

    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private String imageProfile;
    private String rolName;  // Nombre del rol, como "ADMIN" o "USER"
}
