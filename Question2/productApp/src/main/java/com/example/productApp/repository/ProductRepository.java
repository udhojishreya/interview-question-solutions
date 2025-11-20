package com.example.productApp.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.productApp.entity.Product;

/**
 * @author udhoji.shreya - Repository to manage products
 *
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {

	/**
	 * @return List of existing product codes
	 */
	@Query("SELECT p.productCode FROM Product p WHERE p.productCode IN :productCodes")
	List<Long> findCodesIn(Set<Long> productCodes);

	Page<Product> findByProductCode(Long productCode, Pageable pageable);

	Page<Product> findByCategoryCode(Integer categoryCode, Pageable pageable);

}
