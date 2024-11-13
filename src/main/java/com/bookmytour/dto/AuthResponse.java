package com.bookmytour.dto;

import com.bookmytour.entity.Usuario;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class AuthResponse {

    private String token;
    private Usuario usuario;


}
