package com.bookmytour.controller;

import com.bookmytour.entity.TourCities;
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

    @GetMapping("/{id}")
    public TourCities getTourCityById(@PathVariable int id) {
        return tourCitiesService.getTourCityById(id);
    }

    @PostMapping
    public TourCities createTourCity(@RequestBody TourCities tourCity) {
        return tourCitiesService.saveTourCity(tourCity);
    }

    @PutMapping("/{id}")
    public TourCities updateTourCity(@PathVariable int id, @RequestBody TourCities tourCity) {
        tourCity.setId(id);
        return tourCitiesService.saveTourCity(tourCity);
    }

    @DeleteMapping("/{id}")
    public void deleteTourCity(@PathVariable int id) {
        tourCitiesService.deleteTourCity(id);
    }
}
