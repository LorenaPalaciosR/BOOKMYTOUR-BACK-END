package com.bookmytour.service;
import com.bookmytour.entity.Usuario;
import java.util.List;

public interface IUsuarioService {
    List<Usuario> getAllUsuarios();
    Usuario getUsuarioById(int id);
    Usuario saveUsuario(Usuario usuario);
    void deleteUsuario(int id);

}
