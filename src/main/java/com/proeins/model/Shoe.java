package com.proeins.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "Shoe")
public class Shoe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	private Long id;
	
	private String articleNumber;
	
	private String name;
	
	private String brand;
	
	private String color;
	
	private String size;
	
	private String stock;
	
	public Long getId() {
		return id;
	}

	public String getArticleNumber() {
		return articleNumber;
	}


	public String getName() {
		return name;
	}

	public String getBrand() {
		return brand;
	}

	public String getColor() {
		return color;
	}

	public String getSize() {
		return size;
	}

	public String getStock() {
		return stock;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public void setArticleNumber(String articleNumber) {
		this.articleNumber = articleNumber;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public boolean isEmpty() {
		return (articleNumber == null && name == null && brand == null && color == null && size == null && stock == null);
	}
}
