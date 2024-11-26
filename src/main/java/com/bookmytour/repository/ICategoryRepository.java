package com.bookmytour.repository;

import com.bookmytour.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ICategoryRepository extends JpaRepository<Category, Integer>{
    Category findByName(String name);
}
