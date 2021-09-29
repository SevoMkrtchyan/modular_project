package com.example.common.service;



import com.example.common.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    boolean saveCategory(Category category);

    Category findCategoryById(int id);

    boolean deleteCategoryById(int id);

    Category findByName(String name);

}
