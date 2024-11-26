package com.bookmytour.repository;

import com.bookmytour.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ITourRepository extends JpaRepository<Tour, Integer>{

    Tour findByName(String name);

    @Query("SELECT t FROM Tour t " +
            "LEFT JOIN FETCH t.tourCities tc " +
            "LEFT JOIN FETCH tc.city " +
            "WHERE t.tourId = :id")
    Tour findTourWithCitiesById(@Param("id") Integer id);

}
