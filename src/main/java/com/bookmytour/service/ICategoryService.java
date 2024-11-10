package com.bookmytour.service;

import com.bookmytour.entity.Category;

import java.util.List;

public interface ICategoryService {

    List<Category> getAllCategories();
    Category getCategoryById(int id);

    Category saveCategory(Category category);

    void deleteCategory(int id);
}
