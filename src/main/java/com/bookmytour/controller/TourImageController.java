package com.bookmytour.controller;

import com.bookmytour.entity.TourImage;
import com.bookmytour.service.ITourImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tour-images")

public class TourImageController {

    @Autowired
    private ITourImageService tourImageService;

    @GetMapping
    public List<TourImage> getAllTourImages() {
        return tourImageService.getAllTourImages();
    }

    @GetMapping("/{id}")
    public TourImage getTourImageById(@PathVariable int id) {
        return tourImageService.getTourImageById(id);
    }

    @PostMapping
    public TourImage createTourImage(@RequestBody TourImage tourImage) {
        return tourImageService.saveTourImage(tourImage);
    }

    @PutMapping("/{id}")
    public TourImage updateTourImage(@PathVariable int id, @RequestBody TourImage tourImage) {
        tourImage.setImageId(id);
        return tourImageService.saveTourImage(tourImage);
    }

    @DeleteMapping("/{id}")
    public void deleteTourImage(@PathVariable int id) {
        tourImageService.deleteTourImage(id);
    }
}
