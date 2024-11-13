package com.bookmytour.service;
import com.bookmytour.entity.Rol;
import java.util.List;

public interface IRolService {

    Rol getRolById(int id);  // Definir el método aquí

    Rol getOrCreateRol(String rolName);
    List<Rol> getAllRoles();



    Rol saveRol (Rol rol);

    void deleteRol (int id);


}
