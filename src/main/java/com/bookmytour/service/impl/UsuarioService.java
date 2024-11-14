package com.bookmytour.service.impl;
import com.bookmytour.entity.Rol;
import com.bookmytour.entity.Usuario;
import com.bookmytour.repository.IUsuarioRepository;
import com.bookmytour.service.IUsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;


import java.util.List;


@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private RolService rolService; // Servicio para gestionar roles

    // URL de imagen de perfil predeterminada
    private static final String DEFAULT_PROFILE_IMAGE_URL = "https://imagesbucketsback.s3.us-east-1.amazonaws.com/usuariosinfoto.jpg";

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
            adminUser.setImageProfile(DEFAULT_PROFILE_IMAGE_URL);
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
        // Verificar si el correo ya está registrado y pertenece a otro usuario
        Usuario existingUsuario = findByEmail(usuario.getEmail());
        if (existingUsuario != null && !existingUsuario.getUserId().equals(usuario.getUserId())) {
            throw new IllegalArgumentException("El correo ya está registrado con otro usuario.");
        }

        // Asignar imagen de perfil predeterminada si no se proporciona
        if (usuario.getImageProfile() == null || usuario.getImageProfile().isEmpty()) {
            usuario.setImageProfile(DEFAULT_PROFILE_IMAGE_URL);
        }
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

    @Override
    public void assignRole(Usuario usuario, int roleId) {
        Rol newRole = rolService.getRolById(roleId);
        usuario.setRol(newRole);
        usuarioRepository.save(usuario); // Guardar usuario con nuevo rol
    }
}
