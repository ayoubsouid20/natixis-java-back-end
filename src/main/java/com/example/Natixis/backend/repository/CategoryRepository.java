package com.example.Natixis.backend.repository;

import com.example.Natixis.backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository  extends JpaRepository<Category, Long> {
}
