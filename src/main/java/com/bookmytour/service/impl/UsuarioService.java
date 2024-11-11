package com.bookmytour.service.impl;
import com.bookmytour.entity.Rol;
import com.bookmytour.entity.Usuario;
import com.bookmytour.repository.IUsuarioRepository;
import com.bookmytour.service.IUsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;


import java.util.List;


@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private RolService rolService; // Servicio para gestionar roles

    @PostConstruct
    public void initializeAdminUser() {
        if (usuarioRepository.count() == 0) {
            Rol adminRol = rolService.getOrCreateRol("ADMIN");
            Usuario adminUser = new Usuario();
            adminUser.setFirstName("Admin");  // Asigna un valor al campo obligatorio
            adminUser.setLastName("Admin");    // Asigna un valor al campo obligatorio
            adminUser.setEmail("lorerios073@gmail.com");
            adminUser.setPassword("securePassword123"); // Cambia la contraseña en producción
            adminUser.setRol(adminRol);
            usuarioRepository.save(adminUser);
        }
    }
    @Override
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario getUsuarioById(int id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public Usuario saveUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteUsuario(int id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

}
