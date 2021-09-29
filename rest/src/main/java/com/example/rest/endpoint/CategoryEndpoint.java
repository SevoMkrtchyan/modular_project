package com.example.rest.endpoint;

import com.example.common.entity.Category;
import com.example.common.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/category")
@Slf4j
public class CategoryEndpoint {

    private final CategoryService categoryService;

    @GetMapping()
    public List<Category> findAll() {
        return categoryService.findAll();
    }

    @PostMapping()
    public ResponseEntity<Category> saveCategory(@RequestBody Category category) {
        if (categoryService.saveCategory(category)) {
            log.info("Category with {} name was saved at", category.getName());
            return ResponseEntity.ok().build();
        }
        log.info("Creating category with name {} failed", category.getName());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Category> deleteCategoryById(@PathVariable(name = "id") int id) {
        if (!categoryService.deleteCategoryById(id)) {
            log.info("Requested to delete category by id {} which does not exist", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Category with {} id was deleted ", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable(name = "id") int id) {
        Category category = categoryService.findCategoryById(id);
        if (category == null) {
            log.info("Requested to get category by id {} which does not exist", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Request successfully done, sending data to response,requested subject : {}", category);
        return ResponseEntity.ok(category);
    }

    @PutMapping()
    public ResponseEntity<Category> updateCategory(@RequestBody Category category) {
        Category fromDB = categoryService.findCategoryById(category.getId());
        if (fromDB != null) {
            if (categoryService.findByName(category.getName()) == null) {
                fromDB.setName(category.getName());
                categoryService.saveCategory(fromDB);
                log.info("Changed category by {} id and name {} to {} name", fromDB.getId(), fromDB.getName(), category.getName());
                return ResponseEntity.ok().build();
            }
            log.info("Request not completed because category with name {} already exist", category.getName());
        }
        log.info("Request not completed because category with id {} not exist", category.getId());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}