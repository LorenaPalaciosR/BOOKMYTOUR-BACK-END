package com.bookmytour.service;
import com.bookmytour.entity.Rol;
import java.util.List;

public interface IRolService {


    Rol getOrCreateRol(String rolName);
    List<Rol> getAllRoles();

    Rol getRolById (int id);

    Rol saveRol (Rol rol);

    void deleteRol (int id);


}
