package com.bookmytour.controller;

import com.bookmytour.service.impl.DatabaseCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class DatabaseCheckController {
    @Autowired
    private DatabaseCheckService databaseCheckService;

    @GetMapping("/api/check-database")
    public String checkDatabaseConnection() {
        boolean isConnected = databaseCheckService.isDatabaseConnected();
        return isConnected ? "Conexión exitosa a la base de datos" : "Error en la conexión a la base de datos";
    }
}
