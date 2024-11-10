package com.bookmytour.controller;

import com.bookmytour.entity.Tour;
import com.bookmytour.service.ITourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")

public class TourController {

    @Autowired
    private ITourService tourService;

    @GetMapping
    public List<Tour> getAllTours() {
        return tourService.getAllTours();
    }

    @GetMapping("/{id}")
    public Tour getTourById(@PathVariable int id) {
        return tourService.getTourById(id);
    }

    @PostMapping
    public Tour createTour(@RequestBody Tour tour) {
        return tourService.saveTour(tour);
    }

    @PutMapping("/{id}")
    public Tour updateTour(@PathVariable int id, @RequestBody Tour tour) {
        tour.setTourId(id);
        return tourService.saveTour(tour);
    }

    @DeleteMapping("/{id}")
    public void deleteTour(@PathVariable int id) {
        tourService.deleteTour(id);
    }
}
