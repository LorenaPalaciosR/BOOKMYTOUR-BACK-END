package com.bookmytour.repository;
import com.bookmytour.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRolRepository extends JpaRepository<Rol,Integer> {
    Optional<Rol> findByRolName(String rolName);
}
