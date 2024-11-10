package com.bookmytour.service.impl;

import com.bookmytour.entity.City;
import com.bookmytour.repository.ICityRepository;
import com.bookmytour.service.ICityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CityService implements ICityService {

    @Autowired
    private ICityRepository cityRepository;

    @Override
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    @Override
    public City getCityById(int id) {
        return cityRepository.findById(id).orElse(null);
    }

    @Override
    public City saveCity(City city) {
        return cityRepository.save(city);
    }

    @Override
    public void deleteCity(int id) {
        cityRepository.deleteById(id);
    }
}
