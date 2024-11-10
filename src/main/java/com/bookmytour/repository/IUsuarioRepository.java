package com.bookmytour.repository;

import com.bookmytour.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario,Integer>{

    Optional<Usuario> findByEmail(String email);

}
