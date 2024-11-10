package com.bookmytour.controller;

import com.bookmytour.entity.TourFeature;
import com.bookmytour.service.ITourFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tour-features")

public class TourFeatureController {

    @Autowired
    private ITourFeatureService tourFeatureService;

    @GetMapping
    public List<TourFeature> getAllTourFeatures() {
        return tourFeatureService.getAllTourFeatures();
    }

    @GetMapping("/{id}")
    public TourFeature getTourFeatureById(@PathVariable int id) {
        return tourFeatureService.getTourFeatureById(id);
    }

    @PostMapping
    public TourFeature createTourFeature(@RequestBody TourFeature tourFeature) {
        return tourFeatureService.saveTourFeature(tourFeature);
    }

    @PutMapping("/{id}")
    public TourFeature updateTourFeature(@PathVariable int id, @RequestBody TourFeature tourFeature) {
        tourFeature.setFeatureId(id);
        return tourFeatureService.saveTourFeature(tourFeature);
    }

    @DeleteMapping("/{id}")
    public void deleteTourFeature(@PathVariable int id) {
        tourFeatureService.deleteTourFeature(id);
    }
}
