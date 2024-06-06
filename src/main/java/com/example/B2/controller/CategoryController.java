package com.example.B2.controller;

import com.example.B2.model.Category;
import com.example.B2.service.CategoryService;
import com.example.B2.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping("/categories")
    public String getAllCategories(Model model){
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "/categories/categories-list";
    }
    @GetMapping("categories/add")
    public String showAddForm(Model model){
        model.addAttribute("category", new Category());
        return "/categories/add-category";
    }
    @PostMapping("/categories/add")
    public String addCategory(@Valid Category category, BindingResult result){
        if(result.hasErrors()){
            return "/categories/add-category";
        }
        categoryService.addCategory(category);
        return "redirect:/categories";
    }
    @GetMapping("categories/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model){
        Category category  = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        model.addAttribute("category", category);
        return "/categories/update-category";
    }
    @PostMapping("categories/edit/{id}")
    public String updateCategory(@PathVariable Long id, Model model, @Valid Category category, BindingResult result){
        if(result.hasErrors()) {
            category.setId(id);
            return "/categories/update-category";
        }
        categoryService.updateCategory(category);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/categories";
    }
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:"
                        + id));
        categoryService.deleteCategoryById(id);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/categories";
    }
}
