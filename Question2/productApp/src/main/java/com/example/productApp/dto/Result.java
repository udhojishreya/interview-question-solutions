package com.example.productApp.dto;

import java.util.ArrayList;
import java.util.List;

public class Result {
	private int skippedProducts = 0;
	private int skippedCategories = 0;
	private int createdProducts = 0;
	private int createdCategories = 0;
	private List<String> errors = new ArrayList<>();
	private List<String> skippedReasons = new ArrayList<String>();

	public void incrementCreatedProducts() {
		++createdProducts;
	}

	public void incrementCreatedCategories() {
		++createdCategories;
	}

	public void incrementSkippedProducts() {
		++skippedProducts;
	}

	public void incrementSkippedCategories() {
		++skippedCategories;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void addErrors(String error) {
		this.errors.add(error);
	}

	public int getSkippedProducts() {
		return skippedProducts;
	}

	public int getSkippedCategories() {
		return skippedCategories;
	}

	public int getCreatedProducts() {
		return createdProducts;
	}

	public int getCreatedCategories() {
		return createdCategories;
	}

	public void decrementCreatedProducts() {
		--createdProducts;
	}

	public void decrementCreatedCategories() {
		--createdCategories;
	}

	public List<String> getSkippedReasons() {
		return skippedReasons;
	}

	public void addSkippedReasons(String skipReason) {
		skippedReasons.add(skipReason);
	}

}
