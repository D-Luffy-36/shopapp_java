package com.demo.shopapp.repositorys;


import com.demo.shopapp.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Boolean existsByName(String name);
    Optional<Category> findCategoryById(Long id);

    // Phương thức hỗ trợ phân trang
    Page<Category> findAll(Pageable pageable);
}
