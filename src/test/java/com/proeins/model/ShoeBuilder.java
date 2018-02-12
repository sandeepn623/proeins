package com.proeins.model;

public class ShoeBuilder {
	
	private Shoe shoe;
	
	public ShoeBuilder() {
		shoe = new Shoe();
	}
	
	public ShoeBuilder setId(long id) {
		shoe.setId(id);
		return this;
	}
	
	public ShoeBuilder setArticleNumber(String articleNumber) {
		shoe.setArticleNumber(articleNumber);
		return this;
	}

	public ShoeBuilder setName(String name) {
		shoe.setName(name);
		return this;
	}

	public ShoeBuilder setBrand(String brand) {
		shoe.setBrand(brand);
		return this;
	}

	public ShoeBuilder setColor(String color) {
		shoe.setColor(color);
		return this;
	}

	public ShoeBuilder setSize(String size) {
		shoe.setSize(size);
		return this;
	}

	public ShoeBuilder setStock(String stock) {
		shoe.setStock(stock);
		return this;
	}
	
	public Shoe build() {
		return shoe;
	}
}
