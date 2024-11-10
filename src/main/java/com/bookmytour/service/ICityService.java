package com.bookmytour.service;

import com.bookmytour.entity.City;

import java.util.List;

public interface ICityService {

    List<City> getAllCities();

    City getCityById(int id);

    City saveCity(City city);

    void deleteCity(int id);
}
