package com.bookmytour.repository;

import com.bookmytour.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioRepository extends JpaRepository<Usuario,Integer>{
}
