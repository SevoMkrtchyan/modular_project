package com.example.common.service.impl;

import com.example.common.entity.Category;
import com.example.common.repository.CategoryRepository;
import com.example.common.repository.ListingRepository;
import com.example.common.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final ListingRepository listingRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public boolean saveCategory(Category category) {
        if (!categoryRepository.findByName(category.getName().toUpperCase()).isPresent()) {
            category.setName(category.getName().toUpperCase());
            categoryRepository.save(category);
            return true;
        }
        return false;
    }

    @Override
    public Category findCategoryById(int id) {
        Optional<Category> byId = categoryRepository.findById(id);
        return byId.orElse(null);
    }

    @Override
    public boolean deleteCategoryById(int id) {
        if (categoryRepository.findById(id).isPresent()) {
            listingRepository.changeListingCategoryNullWhenCategoryDeleted(id, null);
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Category findByName(String name) {
        Optional<Category> byName = categoryRepository.findByName(name);
        return byName.orElse(null);
    }

}
