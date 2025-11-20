package com.example.productApp.service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.productApp.entity.Category;
import com.example.productApp.entity.Product;
import com.example.productApp.dto.CategoryDto;
import com.example.productApp.dto.ProductDto;
import com.example.productApp.dto.Result;
import com.example.productApp.repository.CategoryRepository;
import com.example.productApp.repository.ProductRepository;

import jakarta.transaction.Transactional;

/**
 * Service class - Business logic to read & process CSV file, create product &
 * category data, perform data validations & store in database in normalized
 * format.
 * 
 * @author udhoji.shreya
 *
 */
@Service
@Transactional
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	/**
	 * This method is invoked by the controller. It reads CSV data from .csv file
	 * present in the resources. It creates product & category objects for the data
	 * present in CSV. It performs validations on product & category objects before
	 * insertion. It inserts data into the database.
	 * 
	 * @returns Result
	 */
	public Result loadCsvData() {
		Result result = new Result();

		// CSV Parsing
		List<CSVRecord> csvRecords = getCsvRecords(result);

		if (Objects.nonNull(csvRecords)) {
			Set<Product> productList = new HashSet<Product>();
			Set<Category> categoryList = new HashSet<Category>();
			Set<Integer> categoryCodes = new HashSet<Integer>();
			Set<Integer> productCategoryCodes = new HashSet<Integer>();
			Set<Long> productCodes = new HashSet<Long>();

			// Object Creation for Products & Categories
			csvRecords.forEach(record -> {
				createCategory(record.get("CATEGORY_CODE"), record.get("CATEGORY_NAME"), categoryList, result,
						categoryCodes);
			});
			csvRecords.forEach(record -> {
				createProduct(record.get("PRODUCT_CODE"), record.get("PRODUCT_NAME"),
						record.get("PRODUCT_CATEGORY_CODE"), productList, result, productCodes, productCategoryCodes);
			});

			// Validations for New Products & Categories
			validateProducts(productList, productCodes, result);
			validateCategory(productList, categoryList, categoryCodes, result, productCategoryCodes);

			// Database Insertion for New Categories & Products
			insertCategories(categoryList);
			insertProducts(productList);
		}
		return result;
	}

	/*
	 * This method uses Apache Commons for CSV Parsing logic. It skips processing
	 * the first line because it is a header. It parses every line in the CSV and
	 * returns CSVRecords that can be iterated over.
	 */
	private List<CSVRecord> getCsvRecords(Result result) {
		ClassPathResource resource = new ClassPathResource("csv/TestExampleFile.csv");
		try (Reader input = new InputStreamReader(resource.getInputStream())) {
			CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(input);
			return parser.getRecords();
		} catch (Exception e) {
			result.addErrors(e.getMessage());
			return null;
		}
	}

	/*
	 * Inserts all products into Database table.
	 */
	private void insertProducts(Set<Product> productList) {
		if (productList.size() > 0)
			productRepository.saveAll(productList);
	}

	/*
	 * Inserts all categories into Database table.
	 */
	private void insertCategories(Set<Category> categoryList) {
		if (categoryList.size() > 0)
			categoryRepository.saveAll(categoryList);
	}

	/*
	 * Validation 1 - Ensure that categories with only new category codes are
	 * processed. Categories with existing category codes will not be processed. Can
	 * be enhanced to update existing category in database if required.
	 * 
	 * Validation 2 - Ensure that category code inserted in productList exists in
	 * categoryList or Database table. Products with non-existent category codes
	 * will not be processed.
	 */
	private void validateCategory(Set<Product> productList, Set<Category> categoryList, Set<Integer> categoryCodes,
			Result result, Set<Integer> productCategoryCodes) {
		productCategoryCodes.addAll(categoryCodes);
		List<Integer> existingCodes = categoryRepository.findCodesIn(productCategoryCodes);
		categoryList.forEach(item -> {
			if (existingCodes.contains(item.getCategoryCode())) {
				result.incrementSkippedCategories();
				result.decrementCreatedCategories();
				result.addSkippedReasons("Category code " + item.getCategoryCode() + " already exists in Database!");
			}
		});
		categoryList.removeIf(category -> existingCodes.contains(category.getCategoryCode()));
		productList.forEach(prd -> {
			if (!existingCodes.contains(prd.getCategoryCode()) && !categoryCodes.contains(prd.getCategoryCode())) {
				result.incrementSkippedProducts();
				result.decrementCreatedProducts();
				result.addSkippedReasons(
						"Category code " + prd.getCategoryCode() + " invalid for Product " + prd.getProductCode());
			}
		});
		productList.removeIf(prd -> !existingCodes.contains(prd.getCategoryCode()));
	}

	/*
	 * Validation - Ensure that products with only new product codes are processed.
	 * Products with existing product codes will not be processed. Can be enhanced
	 * to update existing product in database if required.
	 */
	private void validateProducts(Set<Product> productList, Set<Long> productCodes, Result result) {
		List<Long> existingCodes = productRepository.findCodesIn(productCodes);
		productList.forEach(item -> {
			if (existingCodes.contains(item.getProductCode())) {
				result.incrementSkippedProducts();
				result.decrementCreatedProducts();
				result.addSkippedReasons("Product code " + item.getProductCode() + " already exists in Database!");
			}
		});
		productList.removeIf(product -> existingCodes.contains(product.getProductCode()));
	}

	/*
	 * This method creates Category objects based on the data in CSV Records and
	 * inserts into categoryList which is a List. Records with blank data and
	 * duplicates are skipped.
	 */
	private void createCategory(String categoryCode, String categoryName, Set<Category> categoryList, Result result,
			Set<Integer> categoryCodes) {
		if (categoryCode.isBlank() || categoryName.isBlank()) {
			result.incrementSkippedCategories();
			if (categoryCode.isBlank() && !categoryName.isBlank())
				result.addSkippedReasons("Category Code is blank for Category Name " + categoryName);
			else if (!categoryCode.isBlank() && categoryName.isBlank())
				result.addSkippedReasons("Category Name is blank for Category Code " + categoryCode);
			else
				result.addSkippedReasons("Category Name & Category Code is blank");
		} else {
			Integer categoryCd = Integer.parseInt(categoryCode);
			if (categoryCodes.contains(categoryCd)) {
				result.incrementSkippedCategories();
				result.addSkippedReasons("Category Code " + categoryCode + " duplicate");
			} else {
				Category category = new Category();
				category.setCategoryCode(categoryCd);
				category.setCategoryName(categoryName);
				category.setCreationDate(new Date(System.currentTimeMillis()));
				categoryList.add(category);
				categoryCodes.add(categoryCd);
				result.incrementCreatedCategories();
			}
		}
	}

	/*
	 * This method creates Product objects based on the data in CSV Records and
	 * inserts into productList which is a List. Stream operation skips processing
	 * duplicates if any.
	 */
	private void createProduct(String productCode, String productName, String productCategoryCode,
			Set<Product> productList, Result result, Set<Long> productCodes, Set<Integer> categoryCodes) {
		if (productCode.isBlank() || productName.isBlank() || productCategoryCode.isBlank()) {
			result.incrementSkippedProducts();
			if (productCode.isBlank() && !productName.isBlank())
				result.addSkippedReasons("Product Code is blank for Product Name " + productName);
			else if (!productCode.isBlank() && productName.isBlank())
				result.addSkippedReasons("Product Name is blank for Product Code " + productCode);
			else if (productCategoryCode.isBlank())
				result.addSkippedReasons("Product Category Code is blank for Product Code " + productCode);
			else
				result.addSkippedReasons("Product Details blank");
		} else {
			Long productCd = Long.parseLong(productCode);
			if (productCodes.contains(productCd)) {
				result.incrementSkippedProducts();
				result.addSkippedReasons("Product Code " + productCd + " duplicate");
			} else {
				Product product = new Product();
				product.setProductCode(productCd);
				product.setName(productName);
				product.setCategoryCode(Integer.parseInt(productCategoryCode));
				product.setCreationDate(new Date(System.currentTimeMillis()));
				productList.add(product);
				productCodes.add(productCd);
				categoryCodes.add(product.getCategoryCode());
				result.incrementCreatedProducts();
			}
		}
	}

	/**
	 * @param productCode
	 * @param pageable
	 * @return ProductDto
	 */
	public Page<ProductDto> getProducts(Long productCode, Pageable pageable) {
		Page<Product> page;
		if (productCode != null)
			page = productRepository.findByProductCode(productCode, pageable);
		else
			page = productRepository.findAll(pageable);
		return page.map(p -> new ProductDto(p.getId(), p.getName(), p.getProductCode(), p.getCategoryCode(),
				p.getCreationDate()));
	}

	/**
	 * @param categoryCode
	 * @param pageable
	 * @return CategoryDto
	 */
	public Page<CategoryDto> getCategories(Integer categoryCode, Pageable pageable) {
		Page<Category> page;
		if (categoryCode != null)
			page = categoryRepository.findByCategoryCode(categoryCode, pageable);
		else
			page = categoryRepository.findAll(pageable);
		return page.map(p -> new CategoryDto(p.getId(), p.getCategoryName(), p.getCategoryCode(), p.getCreationDate()));
	}

	/**
	 * @param categoryCode
	 * @param pageable
	 * @return ProductDto
	 */
	public Page<ProductDto> getProductsByCategory(Integer categoryCode, Pageable pageable) {
		Page<Product> page;
		page = productRepository.findByCategoryCode(categoryCode, pageable);
		return page.map(p -> new ProductDto(p.getId(), p.getName(), p.getProductCode(), p.getCategoryCode(),
				p.getCreationDate()));
	}

}
