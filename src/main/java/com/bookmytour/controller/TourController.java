package com.bookmytour.controller;

import com.bookmytour.dto.TourDTO;
import com.bookmytour.entity.Tour;
import com.bookmytour.service.ITourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tours")

public class TourController {

    @Autowired
    private ITourService tourService;

   // Obtener todos los tours (abierto para cualquier usuario)
    @GetMapping
    public List<TourDTO> getAllTours() {
        return tourService.getAllTours().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Obtener un tour por ID (abierto para cualquier usuario)
    @GetMapping("/{id}")
    public ResponseEntity<TourDTO> getTourById(@PathVariable int id) {
        Tour tour = tourService.getTourById(id);
        if (tour == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(convertToDTO(tour));
    }

    // Crear un nuevo tour (solo administrador)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TourDTO> createTour(@RequestBody Tour tour) {
        Tour savedTour = tourService.saveTour(tour);
        return new ResponseEntity<>(convertToDTO(savedTour), HttpStatus.CREATED);
    }

    // Actualizar un tour (solo administrador)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TourDTO> updateTour(@PathVariable int id, @RequestBody Tour tourDetails) {
        Tour tour = tourService.getTourById(id);
        if (tour == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Actualizar los detalles del tour
        tour.setName(tourDetails.getName());
        tour.setDescription(tourDetails.getDescription());
        tour.setSummary(tourDetails.getSummary());
        tour.setDuration(tourDetails.getDuration());
        tour.setItinerary(tourDetails.getItinerary());
        tour.setDatesAvailable(tourDetails.getDatesAvailable());
        tour.setCostPerPerson(tourDetails.getCostPerPerson());

        Tour updatedTour = tourService.saveTour(tour);
        return ResponseEntity.ok(convertToDTO(updatedTour));
    }

    // Eliminar un tour (solo administrador)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTour(@PathVariable int id) {
        if (tourService.getTourById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tour no encontrado");
        }
        tourService.deleteTour(id);
        return ResponseEntity.ok("Tour eliminado con éxito");
    }

    // Método de conversión de Tour a TourDTO para simplificar la respuesta
    private TourDTO convertToDTO(Tour tour) {
        TourDTO dto = new TourDTO();
        dto.setTourId(tour.getTourId());
        dto.setCategoryName(tour.getCategory().getName()); // Suponiendo que la entidad Category tiene un campo "name"
        dto.setName(tour.getName());
        dto.setDescription(tour.getDescription());
        dto.setSummary(tour.getSummary());
        dto.setDuration(tour.getDuration());
        dto.setItinerary(tour.getItinerary());
        dto.setDatesAvailable(tour.getDatesAvailable());
        dto.setCostPerPerson(tour.getCostPerPerson());
        return dto;
    }
}
