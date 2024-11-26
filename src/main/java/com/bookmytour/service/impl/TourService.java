package com.bookmytour.service.impl;

import com.bookmytour.entity.Tour;
import com.bookmytour.repository.ITourRepository;
import com.bookmytour.service.ITourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourService implements ITourService {
    @Autowired
    private ITourRepository tourRepository;
    @Override
    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    @Override
    public Tour getTourById(int id) {
        return tourRepository.findById(id).orElse(null);
    }

   @Override
    public Tour saveTour(Tour tour) {
        return tourRepository.save(tour);
    }

    @Override
    public void deleteTour(int id) {
        tourRepository.deleteById(id);
    }

    @Override
    public Tour getTourByName(String name) {
        return tourRepository.findByName(name);
    }

    @Override
    public Tour getTourWithCities(Integer tourId) {
        Tour tour = tourRepository.findTourWithCitiesById(tourId);
        if (tour != null) {
            System.out.println("Tour encontrado: " + tour.getName());
            System.out.println("Ciudades asociadas: " + tour.getTourCities().stream()
                    .map(tc -> tc.getCity().getName())
                    .collect(Collectors.toList()));
        } else {
            System.out.println("No se encontró el tour con ID: " + tourId);
        }
        return tour;
    }
}