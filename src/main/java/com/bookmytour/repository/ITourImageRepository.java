package com.bookmytour.repository;

import com.bookmytour.entity.TourImage;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ITourImageRepository extends JpaRepository<TourImage, Integer>{
}
