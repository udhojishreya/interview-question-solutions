package com.example.productApp.dto;

import java.util.Date;

public class ProductDto {

	private Integer id;
	private String name;
	private Long productCode;
	private Integer categoryCode;
	private Date creationDate;

	public ProductDto(Integer id, String name, Long productCode, Integer categoryCode, java.sql.Date creationDate) {
		super();
		this.id = id;
		this.name = name;
		this.productCode = productCode;
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

	public Long getProductCode() {
		return productCode;
	}

	public void setProductCode(Long productCode) {
		this.productCode = productCode;
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
