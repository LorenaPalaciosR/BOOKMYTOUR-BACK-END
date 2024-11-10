package com.bookmytour.service.impl;

import com.bookmytour.entity.TourFeature;
import com.bookmytour.repository.ITourFeatureRepository;
import com.bookmytour.service.ITourFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TourFeatureService implements ITourFeatureService {

    @Autowired
    private ITourFeatureRepository tourFeatureRepository;
    @Override
    public List<TourFeature> getAllTourFeatures() {
        return tourFeatureRepository.findAll();
    }

    @Override
    public TourFeature getTourFeatureById(int id) {
        return tourFeatureRepository.findById(id).orElse(null);
    }

    @Override
    public TourFeature saveTourFeature(TourFeature tourFeature) {
        return tourFeatureRepository.save(tourFeature);
    }

    @Override
    public void deleteTourFeature(int id) {
        tourFeatureRepository.deleteById(id);
    }
}
