package com.bookmytour.service;

import com.bookmytour.entity.TourImage;

import java.util.List;

public interface ITourImageService {

    List<TourImage> getAllTourImages();
    TourImage getTourImageById(int id);
    TourImage saveTourImage(TourImage tourImage);
    void deleteTourImage(int id);
}
