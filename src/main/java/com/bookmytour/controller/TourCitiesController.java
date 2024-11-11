package com.bookmytour.controller;

import com.bookmytour.entity.City;
import com.bookmytour.entity.Tour;
import com.bookmytour.entity.TourCities;
import com.bookmytour.entity.TourCitiesId;
import com.bookmytour.service.ITourCitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tour-cities")

public class TourCitiesController {

    @Autowired
    private ITourCitiesService tourCitiesService;

    @GetMapping
    public List<TourCities> getAllTourCities() {
        return tourCitiesService.getAllTourCities();
    }

    @GetMapping("/{tourId}/{cityId}")
    public TourCities getTourCityById(@PathVariable int tourId, @PathVariable int cityId) {
        TourCitiesId id = new TourCitiesId(tourId, cityId);
        return tourCitiesService.getTourCityById(id);
    }

    @PostMapping
    public TourCities createTourCity(@RequestBody TourCities tourCity) {
        return tourCitiesService.saveTourCity(tourCity);
    }

    @PutMapping("/{tourId}/{cityId}")
    public TourCities updateTourCity(@PathVariable int tourId, @PathVariable int cityId, @RequestBody TourCities tourCity) {
        tourCity.setTour(new Tour(tourId)); // Configurar tour con tourId
        tourCity.setCity(new City(cityId)); // Configurar city con cityId
        return tourCitiesService.saveTourCity(tourCity);
    }

    @DeleteMapping("/{tourId}/{cityId}")
    public void deleteTourCity(@PathVariable int tourId, @PathVariable int cityId) {
        TourCitiesId id = new TourCitiesId(tourId, cityId);
        tourCitiesService.deleteTourCity(id);
    }

}
