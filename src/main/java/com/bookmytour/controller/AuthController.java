package com.bookmytour.controller;

import com.bookmytour.dto.AuthResponse;
import com.bookmytour.dto.RegisterRequest;
import com.bookmytour.entity.Usuario;
import com.bookmytour.service.IUsuarioService;
import com.bookmytour.security.JwtUtil;
import com.bookmytour.dto.LoginRequest;
import com.bookmytour.service.impl.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")

public class AuthController {

    private final IUsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;


    @Autowired
    public AuthController(IUsuarioService usuarioService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, EmailService emailService) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        Usuario usuario = new Usuario();
        usuario.setFirstName(registerRequest.getFirstName());
        usuario.setLastName(registerRequest.getLastName());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        Usuario savedUsuario = usuarioService.saveUsuario(usuario);

        // Enviar correo de confirmación (opcional)
        String subject = "Confirmación de registro";
        String text = "Hola " + registerRequest.getFirstName() + ",\n\nGracias por registrarte en nuestro servicio.";
        emailService.sendConfirmationEmail(registerRequest.getEmail(), subject, text);

        // Generar token para el usuario registrado
        String token = jwtUtil.generateToken(savedUsuario.getEmail());
        AuthResponse authResponse = new AuthResponse(token, savedUsuario);

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        Usuario usuario = usuarioService.findByEmail(loginRequest.getEmail());

        if (usuario != null && passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            String token = jwtUtil.generateToken(usuario.getEmail());
            AuthResponse authResponse = new AuthResponse(token, usuario);
            return ResponseEntity.ok(authResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
