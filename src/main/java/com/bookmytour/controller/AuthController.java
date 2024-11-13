package com.bookmytour.controller;

import com.bookmytour.dto.AuthResponse;
import com.bookmytour.dto.LoginRequest;
import com.bookmytour.dto.RegisterRequest;
import com.bookmytour.entity.Rol;
import com.bookmytour.entity.Usuario;
import com.bookmytour.service.IRolService;
import com.bookmytour.service.IUsuarioService;
import com.bookmytour.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IUsuarioService usuarioService;
    private final IRolService rolService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthController(IUsuarioService usuarioService, IRolService rolService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // Endpoint para registrar un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        Usuario usuario = new Usuario();
        usuario.setFirstName(registerRequest.getFirstName());
        usuario.setLastName(registerRequest.getLastName());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Asignar el rol "USER" al usuario usando su ID (2) o el nombre del rol "USER"
        Rol userRole = rolService.getRolById(2); // O usa rolService.getOrCreateRol("USER") si el ID no es seguro
        if (userRole == null) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
        usuario.setRol(userRole);

        Usuario savedUsuario = usuarioService.saveUsuario(usuario);

        // Generar token JWT para el usuario registrado
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUsuario.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        // Crear y enviar la respuesta con token y datos del usuario
        AuthResponse authResponse = new AuthResponse(token, savedUsuario);

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    // Endpoint para iniciar sesión
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        Usuario usuario = usuarioService.findByEmail(loginRequest.getEmail());

        if (usuario != null && passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            // Genera el token usando UserDetails para incluir los roles
            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
            String token = jwtUtil.generateToken(userDetails);
            AuthResponse authResponse = new AuthResponse(token, usuario);
            return ResponseEntity.ok(authResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}