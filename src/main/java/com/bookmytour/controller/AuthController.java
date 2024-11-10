package com.bookmytour.controller;

import com.bookmytour.entity.Usuario;
import com.bookmytour.service.IUsuarioService;
import com.bookmytour.security.JwtUtil;
import com.bookmytour.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Endpoint para registrar usuarios
    @PostMapping("/register")
    public Usuario registerUser(@RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));  // Codificar la contraseña
        return usuarioService.saveUsuario(usuario);  // Guardar el usuario en la base de datos
    }

    // Endpoint para iniciar sesión y generar un token JWT
    @PostMapping("/login")
    public String loginUser(@RequestBody LoginRequest loginRequest) {
        Usuario usuario = usuarioService.findByEmail(loginRequest.getEmail());

        if (usuario != null && passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            // Generar y devolver el token JWT si las credenciales son válidas
            return jwtUtil.generateToken(usuario.getEmail());
        } else {
            throw new RuntimeException("Credenciales inválidas");
        }
    }


}
