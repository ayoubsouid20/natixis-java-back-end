package com.example.Natixis.backend.config;

import com.example.Natixis.backend.model.Category;
import com.example.Natixis.backend.model.Product;
import com.example.Natixis.backend.repository.CategoryRepository;
import com.example.Natixis.backend.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {
    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository, CategoryRepository categoryRepository) {
        return args -> {
            Category electronics = new Category("Electronics");
            Category gaming = new Category("Gaming");

            categoryRepository.save(electronics);
            categoryRepository.save(gaming);

            productRepository.save(new Product("Wireless Headphones", "Noise-canceling headphones", 99.99, electronics));
            productRepository.save(new Product("Gaming Mouse", "RGB gaming mouse", 49.99, gaming));

        };
    }
}
