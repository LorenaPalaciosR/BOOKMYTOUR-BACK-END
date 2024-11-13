package com.bookmytour.service.impl;

import com.bookmytour.entity.Usuario;
import com.bookmytour.repository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));

        // Configura el rol del usuario como una lista de GrantedAuthority
        String roleName = "ROLE_" + usuario.getRol().getRolName().toUpperCase();  // Asegúrate de que sea ROLE_ADMIN
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(roleName));

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(), usuario.getPassword(), authorities);
    }

}
