package com.bookmytour.service.impl;
import com.bookmytour.entity.Usuario;
import com.bookmytour.repository.IUsuarioRepository;
import com.bookmytour.service.IUsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;


@Service
public class UsarioService implements IUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

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

}
