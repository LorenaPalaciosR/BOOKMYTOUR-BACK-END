package com.bookmytour.controller;
import com.bookmytour.dto.CityDTO;
import com.bookmytour.entity.City;
import com.bookmytour.service.ICityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    @Autowired
    private ICityService cityService;

    // Obtener todas las ciudades (disponible para todos)
    @GetMapping
    public List<CityDTO> getAllCities() {
        return cityService.getAllCities().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Obtener una ciudad por ID (disponible para todos)
    @GetMapping("/{id}")
    public CityDTO getCityById(@PathVariable int id) {
        City city = cityService.getCityById(id);
        return convertToDTO(city);
    }

    // Crear una ciudad (solo para administradores)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CityDTO createCity(@RequestBody CityDTO cityDTO) {
        City city = convertToEntity(cityDTO);
        City savedCity = cityService.saveCity(city);
        return convertToDTO(savedCity);
    }

    // Actualizar una ciudad (solo para administradores)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public CityDTO updateCity(@PathVariable int id, @RequestBody CityDTO cityDTO) {
        City existingCity = cityService.getCityById(id);
        existingCity.setName(cityDTO.getName());
        City updatedCity = cityService.saveCity(existingCity);
        return convertToDTO(updatedCity);
    }

    // Eliminar una ciudad (solo para administradores)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCity(@PathVariable int id) {
        cityService.deleteCity(id);
    }

    // Método auxiliar para convertir una entidad City a un CityDTO
    private CityDTO convertToDTO(City city) {
        CityDTO cityDTO = new CityDTO();
        cityDTO.setCityId(city.getCityId());
        cityDTO.setName(city.getName());
        return cityDTO;
    }

    // Método auxiliar para convertir un CityDTO a una entidad City
    private City convertToEntity(CityDTO cityDTO) {
        City city = new City();
        city.setCityId(cityDTO.getCityId());
        city.setName(cityDTO.getName());
        return city;
    }
}
