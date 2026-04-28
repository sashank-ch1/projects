package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

@Entity
public class Payment {
	@Id
private Long id;
	@Column(nullable=false)
private String status;
	@Column
	private String userId;
	@Column
	private double amount;
	
	public Payment() {
		
	}
	public Payment(Long id,String status,String userId,double amount) {
		this.id=id;
		this.status=status;
		this.userId=userId;
		this.amount=amount;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Payment [id=" + id + ", status=" + status + "]";
	}
	public void setUserId(String userId) {
		this.userId=userId;
	}
	public String getuserid() {
		return userId;
	}
	public void setAmount(double amount) {
		this.amount=amount;
	}
	public double getamount() {
		return amount;
	}
}
