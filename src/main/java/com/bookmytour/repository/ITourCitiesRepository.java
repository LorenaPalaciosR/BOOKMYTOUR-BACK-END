package com.bookmytour.repository;

import com.bookmytour.entity.TourCities;
import com.bookmytour.entity.TourCitiesId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITourCitiesRepository extends JpaRepository<TourCities, TourCitiesId>{


}
