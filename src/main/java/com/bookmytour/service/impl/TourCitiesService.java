package com.bookmytour.service.impl;

import com.bookmytour.entity.TourCities;
import com.bookmytour.repository.ITourCitiesRepository;
import com.bookmytour.service.ITourCitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
@Service
public class TourCitiesService implements ITourCitiesService {

    @Autowired
    private ITourCitiesRepository tourCitiesRepository;


    @Override
    public List<TourCities> getAllTourCities() {
        return tourCitiesRepository.findAll();
    }

    @Override
    public TourCities getTourCityById(int id) {
        return tourCitiesRepository.findById(id).orElse(null);
    }

    @Override
    public TourCities saveTourCity(TourCities tourCity) {
        return tourCitiesRepository.save(tourCity);
    }

    @Override
    public void deleteTourCity(int id) {
        tourCitiesRepository.deleteById(id);
    }
}
