package com.example.B2.service;

import com.example.B2.model.Product;
import com.example.B2.repository.CategoryRepository;
import com.example.B2.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    @Value("${static.images.directory}")
    private String imagesDirectory;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product addProduct(Product product, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            String imgUrl = saveImage(image);
            product.setImageUrl(imgUrl);
        }
        return productRepository.save(product);
    }

    private String saveImage(MultipartFile image) throws IOException {
        String imageName = UUID.randomUUID().toString() + ".jpg";
        File directory = new File(imagesDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        Path imagePath = Paths.get(imagesDirectory, imageName);
        Files.write(imagePath, image.getBytes());

        // Return the URL of the saved image
        return "/images/product/" + imageName;
    }

    public Product updateProduct(@NotNull Product product, MultipartFile image) throws IOException {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        product.getId() + " does not exist."));

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());

        if (image != null && !image.isEmpty()) {
            String imgUrl = saveImage(image);
            existingProduct.setImageUrl(imgUrl);
        }

        return productRepository.save(existingProduct);
    }

    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        productRepository.deleteById(id);
    }
}
