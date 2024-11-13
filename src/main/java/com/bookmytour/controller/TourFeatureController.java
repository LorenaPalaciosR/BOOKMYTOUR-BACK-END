package com.bookmytour.controller;

import com.bookmytour.entity.TourFeature;
import com.bookmytour.service.ITourFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tour-features")

public class TourFeatureController {

    @Autowired
    private ITourFeatureService tourFeatureService;

    // Permitir a todos los usuarios ver todas las características de los tours
    @GetMapping
    public List<TourFeature> getAllTourFeatures() {
        return tourFeatureService.getAllTourFeatures();
    }

    // Permitir a todos los usuarios ver una característica de tour específica por ID
    @GetMapping("/{id}")
    public TourFeature getTourFeatureById(@PathVariable int id) {
        return tourFeatureService.getTourFeatureById(id);
    }

    // Solo administradores pueden crear una nueva característica de tour
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public TourFeature createTourFeature(@RequestBody TourFeature tourFeature) {
        return tourFeatureService.saveTourFeature(tourFeature);
    }

    // Solo administradores pueden actualizar una característica de tour existente
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public TourFeature updateTourFeature(@PathVariable int id, @RequestBody TourFeature tourFeature) {
        tourFeature.setFeatureId(id);
        return tourFeatureService.saveTourFeature(tourFeature);
    }

    // Solo administradores pueden eliminar una característica de tour
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteTourFeature(@PathVariable int id) {
        tourFeatureService.deleteTourFeature(id);
    }
}
