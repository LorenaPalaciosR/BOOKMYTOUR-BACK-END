package com.bookmytour.repository;

import com.bookmytour.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ICityRepository extends JpaRepository<City, Integer>{
}
