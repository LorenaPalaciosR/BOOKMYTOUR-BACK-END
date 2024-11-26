package com.bookmytour.service;

import com.bookmytour.entity.Tour;
import com.bookmytour.entity.TourCities;

import java.util.List;

public interface ITourService {
    List<Tour> getAllTours();
    Tour getTourById(int id);
    Tour saveTour (Tour tour);
    void deleteTour(int id);

    // Nuevo método
    Tour getTourByName(String name);
}
