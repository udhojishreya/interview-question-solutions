package com.example.productApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.productApp.dto.CategoryDto;
import com.example.productApp.dto.ProductDto;
import com.example.productApp.dto.Result;
import com.example.productApp.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;

/**
 * @author udhoji.shreya - Provides HTTP API to process CSV data
 *
 */
@RestController
@RequestMapping("/product-api/v1")
public class ProductAppController {

	@Autowired
	private ProductService productService;

	/**
	 * Invokes service layer to process CSV data & store CSV data into Database
	 * tables in normalized form.
	 * 
	 * @return Success/Error as per business logic.
	 */
	@PostMapping("/process-csv")
	@Operation(summary = "Start CSV processing", description = "Parses CSV and writes categories & products to DB")
	@ResponseBody
	public ResponseEntity<Result> processCsvData() {
		Result result = productService.loadCsvData();
		if (result.getErrors().size() > 0)
			return ResponseEntity.status(500).body(result);
		return ResponseEntity.ok(result);

	}

	/**
	 * Health check
	 * 
	 * @return 200 OK if app is up and running
	 */
	@GetMapping("/health")
	@Operation(summary = "Performs health check", description = "Performs health check and returns OK")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("OK");
	}

	/**
	 * Search products. Define filters and/or sorting criteria.
	 * 
	 * @return 200 OK with results
	 */
	@GetMapping("/get/products")
	@Operation(summary = "Search products", description = "Search products. Define filters and/or sorting criteria (optional). "
			+ "Supported sort criteria: id, name, productCode, categoryCode, creationDate. "
			+ "Supported sort order: asc, desc")
	public ResponseEntity<Page<ProductDto>> getProducts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id,asc") String sort,
			@RequestParam(required = false) Long productCode) {
		String[] sortParts = sort.split(",");
		Sort sorting = Sort.by(sortParts[0]);
		if (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc"))
			sorting = sorting.descending();
		Pageable pageable = PageRequest.of(page, size, sorting);
		Page<ProductDto> result = productService.getProducts(productCode, pageable);
		return ResponseEntity.ok(result);
	}

	/**
	 * Search products. Define filters and/or sorting criteria.
	 * 
	 * @return 200 OK with results
	 */
	@GetMapping("/get/productsByCategory")
	@Operation(summary = "Search products", description = "Search products by category code. Define filters and/or sorting criteria (optional). "
			+ "Supported sort criteria: id, name, productCode, categoryCode, creationDate. "
			+ "Supported sort order: asc, desc")
	public ResponseEntity<Page<ProductDto>> getProductsByCategory(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id,asc") String sort,
			@RequestParam Integer categoryCode) {
		String[] sortParts = sort.split(",");
		Sort sorting = Sort.by(sortParts[0]);
		if (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc"))
			sorting = sorting.descending();
		Pageable pageable = PageRequest.of(page, size, sorting);
		Page<ProductDto> result = productService.getProductsByCategory(categoryCode, pageable);
		return ResponseEntity.ok(result);
	}

	/**
	 * Search categories. Define filters and/or sorting criteria.
	 * 
	 * @return 200 OK with results
	 */
	@GetMapping("/get/categories")
	@Operation(summary = "Search categories", description = "Search categories. Define filters and/or sorting criteria (optional)."
			+ "Supported sort criteria: id, categoryName, categoryCode, creationDate. "
			+ "Supported sort order: asc, desc")
	public ResponseEntity<Page<CategoryDto>> getCategories(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id,asc") String sort,
			@RequestParam(required = false) Integer categoryCode) {
		String[] sortParts = sort.split(",");
		Sort sorting = Sort.by(sortParts[0]);
		if (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc"))
			sorting = sorting.descending();
		Pageable pageable = PageRequest.of(page, size, sorting);
		Page<CategoryDto> result = productService.getCategories(categoryCode, pageable);
		return ResponseEntity.ok(result);
	}
}
