package com.hms.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.CategoryEntity;
@Repository
public interface CategoryRepostiory
        extends JpaRepository<CategoryEntity,Integer>{

    Optional<CategoryEntity> findByCategoryNameIgnoreCase(String categoryName);

	List<CategoryEntity> findByCategoryNameContainingIgnoreCase(String search);

}