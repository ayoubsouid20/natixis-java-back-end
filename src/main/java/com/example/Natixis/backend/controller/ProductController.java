package com.example.Natixis.backend.controller;

import com.example.Natixis.backend.exceptions.ResourceNotFoundException;
import com.example.Natixis.backend.model.Product;
import com.example.Natixis.backend.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping("/all")
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));

        productRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @PostMapping("/addProduct")
    public ResponseEntity<Product> addProduct(@RequestBody @Valid Product product) {
        var savedProduct = productRepository.save(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PostMapping("/updateProduct/{id}")
    public ResponseEntity<Product> updateProduct(@RequestBody Product updatedProduct, @PathVariable Long id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
        Product newProduct = mergeProduct(product, updatedProduct);
        productRepository.save(newProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }


    // this function is used to update old product with new one
    private Product mergeProduct(Product oldProduct, Product newProduct) {
        if (newProduct.getName() != null) {
            oldProduct.setName(newProduct.getName());
        }
        if (newProduct.getPrice() != null) {
            oldProduct.setPrice(newProduct.getPrice());
        }
        if (newProduct.getDescription() != null) {
            oldProduct.setDescription(newProduct.getDescription());
        }

        return oldProduct;
    }


}
