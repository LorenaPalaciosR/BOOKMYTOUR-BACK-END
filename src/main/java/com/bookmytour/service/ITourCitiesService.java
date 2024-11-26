package com.bookmytour.service;

import com.bookmytour.entity.TourCities;
import com.bookmytour.entity.TourCitiesId;

import java.util.List;

public interface ITourCitiesService {
    List<TourCities> getAllTourCities();
    TourCities getTourCityById (TourCitiesId id);
    TourCities saveTourCity (TourCities tourCity);
    void deleteTourCity(TourCitiesId id);

}
