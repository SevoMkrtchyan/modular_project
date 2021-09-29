package com.example.web.controller;

import com.example.common.entity.Category;
import com.example.common.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public String findAll(ModelMap modelMap) {
        modelMap.addAttribute("categories", categoryService.findAll());
        return "category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@RequestBody Category category) {
        if (categoryService.saveCategory(category)) {
            log.info("Category with {} name was saved at", category.getName());
            return "redirect:/categories";
        }
        log.info("Creating category with name {} failed", category.getName());
        return "redirect:/categories";
    }

    @GetMapping(value = "/deleteCategoryById/{id}")
    public String deleteCategoryById(@PathVariable(name = "id") int id) {
        if (!categoryService.deleteCategoryById(id)) {
            log.info("Requested to delete category by id {} which does not exist", id);
            return "redirect:/categories";
        }
        log.info("Category with {} id was deleted ", id);
        return "redirect:/categories";
    }

    @GetMapping(value = "/getCategoryById/{id}")
    public String getCategoryById(@PathVariable(name = "id") int id, ModelMap modelMap) {
        Category category = categoryService.findCategoryById(id);
        if (category == null) {
            log.info("Requested to get category by id {} which does not exist", id);
            return "redirect:/categories";
        }
        modelMap.addAttribute("category", category);
        log.info("Request successfully done, sending data to response,requested subject : {}", category);
        return "singleCategory";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@RequestBody Category category) {
        Category fromDB = categoryService.findCategoryById(category.getId());
        if (fromDB != null) {
            if (categoryService.findByName(category.getName()) == null) {
                fromDB.setName(category.getName());
                categoryService.saveCategory(fromDB);
                log.info("Changed category by {} id and name {} to {} name", fromDB.getId(), fromDB.getName(), category.getName());
                return "redirect:/getCategoryById/" + fromDB.getId();
            }
            log.info("Request not completed because category with name {} already exist", category.getName());
        }
        log.info("Request not completed because category with id {} not exist", category.getId());
        return "redirect:/categories";

    }
}
