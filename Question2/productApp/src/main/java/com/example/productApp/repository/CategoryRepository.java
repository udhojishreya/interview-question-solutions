package com.example.productApp.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.productApp.entity.Category;

/**
 * @author udhoji.shreya - Repository to manage categories
 *
 */
public interface CategoryRepository extends JpaRepository<Category, Integer> {

	@Query("SELECT c.categoryCode FROM Category c WHERE c.categoryCode IN :categoryCodes")
	List<Integer> findCodesIn(Set<Integer> categoryCodes);

	Page<Category> findAll(Pageable pageable);

	Page<Category> findByCategoryCode(Integer code, Pageable pageable);

}
