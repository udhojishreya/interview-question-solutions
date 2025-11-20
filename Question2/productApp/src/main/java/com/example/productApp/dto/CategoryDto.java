package com.example.productApp.dto;

import java.util.Date;

public class CategoryDto {

	private Integer id;
	private String name;
	private Integer categoryCode;
	private Date creationDate;

	public CategoryDto(Integer id, String categoryName, Integer categoryCode, java.sql.Date creationDate) {
		this.id = id;
		this.name = categoryName;
		this.categoryCode = categoryCode;
		this.creationDate = new Date(creationDate.getTime());
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(Integer categoryCode) {
		this.categoryCode = categoryCode;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}
