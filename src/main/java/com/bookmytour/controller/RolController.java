package com.bookmytour.controller;
import com.bookmytour.dto.RolDTO;
import com.bookmytour.entity.Rol;
import com.bookmytour.service.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private IRolService rolService;

    // Obtener todos los roles (solo para administradores)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<RolDTO> getAllRoles() {
        return rolService.getAllRoles().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Obtener un rol por ID (solo para administradores)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public RolDTO getRolById(@PathVariable int id) {
        Rol rol = rolService.getRolById(id);
        return convertToDTO(rol);
    }

    // Crear un rol (solo para administradores)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public RolDTO createRol(@RequestBody RolDTO rolDTO) {
        Rol rol = convertToEntity(rolDTO);
        Rol savedRol = rolService.saveRol(rol);
        return convertToDTO(savedRol);
    }

    // Actualizar un rol (solo para administradores)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public RolDTO updateRol(@PathVariable int id, @RequestBody RolDTO rolDTO) {
        Rol existingRol = rolService.getRolById(id);
        existingRol.setRolName(rolDTO.getRolName());
        Rol updatedRol = rolService.saveRol(existingRol);
        return convertToDTO(updatedRol);
    }

    // Eliminar un rol (solo para administradores)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteRol(@PathVariable int id) {
        rolService.deleteRol(id);
    }

    // Método auxiliar para convertir una entidad Rol a RolDTO
    private RolDTO convertToDTO(Rol rol) {
        RolDTO rolDTO = new RolDTO();
        rolDTO.setRoleId(rol.getRoleId());
        rolDTO.setRolName(rol.getRolName());
        return rolDTO;
    }

    // Método auxiliar para convertir un RolDTO a una entidad Rol
    private Rol convertToEntity(RolDTO rolDTO) {
        Rol rol = new Rol();
        rol.setRoleId(rolDTO.getRoleId());
        rol.setRolName(rolDTO.getRolName());
        return rol;
    }
}
