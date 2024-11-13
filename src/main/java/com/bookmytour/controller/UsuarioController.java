package com.bookmytour.controller;

import com.bookmytour.dto.UsuarioDTO;
import com.bookmytour.entity.Usuario;
import com.bookmytour.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")  // Limitar acceso a todos los métodos de esta clase solo para ADMIN
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @Autowired
    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Obtener todos los usuarios (solo administrador)
    @GetMapping
    public List<UsuarioDTO> getAllUsuarios() {
        return usuarioService.getAllUsuarios().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Obtener un usuario por ID (solo administrador)
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable int id) {
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(convertToDTO(usuario));
    }

    // Actualizar un usuario (solo administrador)
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> updateUsuario(@PathVariable int id, @RequestBody Usuario usuarioDetails) {
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        usuario.setFirstName(usuarioDetails.getFirstName());
        usuario.setLastName(usuarioDetails.getLastName());
        usuario.setEmail(usuarioDetails.getEmail());
        usuario.setImageProfile(usuarioDetails.getImageProfile());
        usuarioService.saveUsuario(usuario);
        return ResponseEntity.ok(convertToDTO(usuario));
    }

    // Eliminar un usuario (solo administrador)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUsuario(@PathVariable int id) {
        if (usuarioService.getUsuarioById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        usuarioService.deleteUsuario(id);
        return ResponseEntity.ok("Usuario eliminado con éxito");
    }

    // Cambiar el rol de un usuario (solo administrador)
    @PutMapping("/{id}/role")
    public ResponseEntity<String> assignRole(@PathVariable int id, @RequestParam String roleName) {
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario != null) {
            usuarioService.assignRole(usuario, roleName);
            return ResponseEntity.ok("Rol actualizado con éxito");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
    }

    // Método de conversión de Usuario a UsuarioDTO para simplificar la respuesta
    private UsuarioDTO convertToDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setUserId(usuario.getUserId());
        dto.setFirstName(usuario.getFirstName());
        dto.setLastName(usuario.getLastName());
        dto.setEmail(usuario.getEmail());
        dto.setImageProfile(usuario.getImageProfile());
        dto.setRolName(usuario.getRol().getRolName());
        return dto;
    }
}
