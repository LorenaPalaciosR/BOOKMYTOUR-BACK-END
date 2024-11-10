package com.bookmytour.service;

import com.bookmytour.entity.TourCities;

import java.util.List;

public interface ITourCitiesService {
    List<TourCities> getAllTourCities();
    TourCities getTourCityById (int id);
    TourCities saveTourCity (TourCities tourCity);
    void deleteTourCity(int id);
}
