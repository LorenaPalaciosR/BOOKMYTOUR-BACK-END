package com.bookmytour.controller;

import com.bookmytour.entity.Usuario;
import com.bookmytour.service.IUsuarioService;
import com.bookmytour.service.impl.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private RolService rolService;


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/user/{id}/role")
    public String changeUserRole(@PathVariable int id, @RequestParam String roleName) {
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario != null) {
            usuario.setRol(rolService.getOrCreateRol(roleName));
            usuarioService.saveUsuario(usuario);
            return "Rol actualizado con éxito";
        }
        return "Usuario no encontrado";
    }
}

