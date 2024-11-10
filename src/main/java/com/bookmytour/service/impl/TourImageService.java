package com.bookmytour.service.impl;

import com.bookmytour.entity.TourImage;
import com.bookmytour.repository.ITourImageRepository;
import com.bookmytour.service.ITourImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service

public class TourImageService implements ITourImageService {

    @Autowired
    private ITourImageRepository tourImageRepository;
    @Override
    public List<TourImage> getAllTourImages() {
        return tourImageRepository.findAll();
    }

    @Override
    public TourImage getTourImageById(int id) {
        return tourImageRepository.findById(id).orElse(null);
    }

    @Override
    public TourImage saveTourImage(TourImage tourImage) {
        return tourImageRepository.save(tourImage);
    }

    @Override
    public void deleteTourImage(int id) {
        tourImageRepository.deleteById(id);
    }
}
