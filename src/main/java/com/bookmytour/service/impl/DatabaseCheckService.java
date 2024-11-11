package com.bookmytour.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
@Service
public class DatabaseCheckService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean isDatabaseConnected() {
        try {
            // Realiza una consulta simple para verificar la conexión
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            System.out.println("Conexión a la base de datos exitosa.");
            return true;
        } catch (Exception e) {
            System.out.println("Error en la conexión a la base de datos.");
            e.printStackTrace();
            return false;
        }
    }
}
