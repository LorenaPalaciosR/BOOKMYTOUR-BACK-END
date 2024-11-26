package com.bookmytour.repository;

import com.bookmytour.entity.TourImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ITourImageRepository extends JpaRepository<TourImage, Integer>{

    @Query("SELECT ti FROM TourImage ti WHERE ti.tour.tourId = :tourId")
    List<TourImage> findByTourId(@Param("tourId") int tourId);
}
