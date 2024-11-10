package com.bookmytour.repository;

import com.bookmytour.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITourRepository extends JpaRepository<Tour, Integer>{
}
