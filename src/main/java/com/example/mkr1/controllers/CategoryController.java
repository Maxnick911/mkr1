package com.example.mkr1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.mkr1.domain.*;
import com.example.mkr1.repos.*;
import java.util.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepo categoryRepo;

    @Autowired
    public CategoryController(CategoryRepo categoryRepo){
        this.categoryRepo = categoryRepo;
    }

    @GetMapping("/")
    public String showCategory(Map<String, Object> model) {
        Iterable<Category> categories = categoryRepo.findAll();

        model.put("categories", categories);

        return "categories";
    }

    @PostMapping("/add")
    public String addCategory(@RequestParam String name){
        if(name.trim().isEmpty()){
            return "redirect:/categories/";
        }
        Category category = new Category(name.trim());
        categoryRepo.save(category);

        return "redirect:/categories/";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id){
        categoryRepo.deleteById(id);

        return "redirect:/categories/";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable("id") Long id, Map<String, Object> model){
        Category category = categoryRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Category id: " + id));

        model.put("category", category);

        return "categories_edit";
    }

    @PostMapping("/save")
    public String saveCategory(@RequestParam Long id,
                               @RequestParam String name){
        Category category = categoryRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid category id: " + id));

        category.setName(name.trim());

        categoryRepo.save(category);

        return "redirect:/categories/";
    }

}
