package com.bookmytour.service.impl;

import com.bookmytour.entity.City;
import com.bookmytour.entity.TourCities;
import com.bookmytour.entity.TourCitiesId;
import com.bookmytour.repository.ICityRepository;
import com.bookmytour.repository.ITourCitiesRepository;
import com.bookmytour.service.ITourCitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
@Service
public class TourCitiesService implements ITourCitiesService {

    @Autowired
    private ITourCitiesRepository tourCitiesRepository;

    @Autowired
    private ICityRepository cityRepository; // Repositorio de ciudades


    @Override
    public List<TourCities> getAllTourCities() {
        return tourCitiesRepository.findAll();
    }

    @Override
    public TourCities getTourCityById(TourCitiesId id) {
        return tourCitiesRepository.findById(id).orElse(null);
    }

    @Override
    public TourCities saveTourCity(TourCities tourCity) {
        return tourCitiesRepository.save(tourCity);
    }

    @Override
    public void deleteTourCity(TourCitiesId id) {
        tourCitiesRepository.deleteById(id);
    }
    @Override
    public City getCityByName(String cityName) {
        return cityRepository.findByName(cityName); // Buscar ciudad por nombre
    }
}
