package com.example.B2.controller;

import com.example.B2.model.Product;
import com.example.B2.service.CategoryService;
import com.example.B2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService; // Đảm bảo bạn đã inject

    // Đường dẫn thư mục để lưu trữ hình ảnh
    private final String uploadDir = "src/main/resources/static/images/product/";

    // Display a list of all products
    @GetMapping()
    public String showProductList(Model model) {
        List<Product> products = productService.getAllProducts()
                .stream()
                .sorted(Comparator.comparingDouble(Product::getPrice))
                .collect(Collectors.toList());
        model.addAttribute("products", products);
        return "/products/products-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/products/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult result, @RequestParam("image") MultipartFile image) {
        if (result.hasErrors()) {
            return "/products/add-product";
        }
        try {
            productService.addProduct(product, image);
        } catch (Exception e) {
            // Handle image upload exception
            e.printStackTrace();
            return "/products/add-product";
        }
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/products/update-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product,
                                BindingResult result, @RequestParam("image") MultipartFile image) {
        if (result.hasErrors()) {
            product.setId(id);
            return "/products/update-product";
        }
        try {
            productService.updateProduct(product, image);
        } catch (IOException e) {
            e.printStackTrace();
            return "/products/update-product";
        }
        return "redirect:/products";
    }

    // Handle request to delete a product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }
    @GetMapping("/search")
    public String searchByName(@RequestParam("keyword") String keyword, Model model){
        List<Product> products = productService.getAllProducts()
                .stream()
                .filter(p-> p.getName().toLowerCase().contains(keyword.toLowerCase())).collect(Collectors.toList());
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "/products/products-list";
    }

}
