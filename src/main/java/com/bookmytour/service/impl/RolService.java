package com.bookmytour.service.impl;
import com.bookmytour.entity.Rol;
import com.bookmytour.repository.IRolRepository;
import com.bookmytour.service.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService implements IRolService {

    @Autowired
    private IRolRepository rolRepository;

    @Override
    public List<Rol> getAllRoles(){
        return rolRepository.findAll();
    }

    @Override
    public Rol getRolById(int id) {
        return null;
    }

    @Override
    public Rol saveRol(Rol rol){
        return rolRepository.save(rol);
    }

    @Override
    public void deleteRol(int id){
        rolRepository.deleteById(id);
    }
}
