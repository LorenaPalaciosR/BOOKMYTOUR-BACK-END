package com.bookmytour.service;

import com.bookmytour.entity.TourImage;

import java.util.List;

public interface ITourImageService {

    List<TourImage> getAllTourImages();
    TourImage getTourImageById(int id);
    TourImage saveTourImage(TourImage tourImage);

    List<TourImage> getTourImagesByTourId(int tourId);
    void deleteTourImage(int id);
}
