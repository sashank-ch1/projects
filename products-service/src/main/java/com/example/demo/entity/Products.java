package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class Products {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
private Long id;
	@Column(nullable=true,name="product_id")
private String productId;
	@Column(nullable=false)
private String name;
	@Column(nullable=false)	
private int qunty;
	@Column(nullable=false)
private double price;
	
	@Column
	private String category;

	public String getCategory() {
	    return category;
	}

	public void setCategory(String category) {
	    this.category = category;
	}
	
	public Products() {
		
	}
	public Products(Long id,String productId,String name,int qunty,double price) {
		this.id=id;
		this.productId=productId;
		this.name=name;
		this.qunty=qunty;
		this.price=price;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getQunty() {
		return qunty;
	}
	public void setQunty(int qunty) {
		this.qunty = qunty;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	@Override
	public String toString() {
		return "Products [id=" + id + ", productId=" + productId + ", name=" + name + ", qunty=" + qunty + ", price="
				+ price + "]";
	}
	
}
