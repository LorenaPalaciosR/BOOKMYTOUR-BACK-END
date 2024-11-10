package com.bookmytour.service;

import com.bookmytour.entity.TourFeature;
import com.bookmytour.service.impl.TourService;

import java.util.List;

public interface ITourFeatureService {
    List<TourFeature> getAllTourFeatures();
    TourFeature getTourFeatureById(int id);

    TourFeature saveTourFeature(TourFeature tourFeature);

    void deleteTourFeature(int id);
}
