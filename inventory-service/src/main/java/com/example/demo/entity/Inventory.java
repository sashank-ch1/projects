package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class Inventory {
	@Id
private Long id;
	@Column(nullable=false)
private String  productId;
	@Column(nullable=false)
private int qunty;
	
	public Inventory() {
		
	}
	
	public Inventory(Long id,String productId,int qunty) {
		this.id=id;
		this.productId=productId;
		this.qunty=qunty;
	}
	
	
	
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProductid() {
		return productId;
	}
	public void setProductid(String productid) {
		this.productId = productid;
	}
	public int getQunty() {
		return qunty;
	}
	public void setQunty(int qunty) {
		this.qunty = qunty;
	}
	@Override
	public String toString() {
		return "Inventory [id=" + id + ", productid=" + productId + ", qunty=" + qunty + "]";
	}
	

}
