package com.bookmytour.repository;

import com.bookmytour.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ICityRepository extends JpaRepository<City, Integer>{
    City findByName(String name); // Spring Data JPA generará automáticamente la implementación

}
