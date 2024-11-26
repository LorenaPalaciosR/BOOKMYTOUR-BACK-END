package com.bookmytour.service;

import com.bookmytour.entity.Category;

import java.util.List;

public interface ICategoryService {

    List<Category> getAllCategories();
    Category getCategoryById(int id);

    Category saveCategory(Category category);

    Category getCategoryByName(String name);
    void deleteCategory(int id);
}
