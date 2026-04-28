package com.example.demo.entity;

import jakarta.persistence.*;

@Entity

public class Orders {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
private Long id;
	@Column(nullable=false)
    private String productid;
	private String userId;
	@Column(name="orderid",nullable=false,unique=true)
	private String orderid;
	@Column(nullable=false)
	private String orderstatus;
	private String productname;
	private double price;
	private String address;
	private String paymentmode;
	private String paymentstatus;
	private String feedback;
	private int rating;
	public Orders() {
		
	}

	public Orders(Long id,String productid,String orderstatus,String orderid,String productname,double price,String address,String paymentmode,String paymentstatus,String userId,String feedback,int rating) {
		this.id=id;
		this.productid=productid;
		this.orderstatus=orderstatus;
		this.orderid=orderid;
		this.productname=productname;
		this.price=price;
		this.address=address;
		this.paymentmode=paymentmode;
		this.paymentstatus=paymentstatus;
		this.userId=userId;
		this.feedback=feedback;
		this.rating=rating;
	}
	public void setrating(int rating) {
		this.rating=rating;
	}
	public int getrating() {
		return rating;
	}
	public void setfeedback(String feedback) {
		this.feedback=feedback;
	}
	public String getfeedback() {
		return feedback;
	}
	public void setuserid(String userId) {
		this.userId=userId;
	}
	public String getUserId() {
		return userId;
	}
    public void setorderid(String orderid) {
    	this.orderid=orderid;
    }
    public String getorderid() {
    	return orderid;
    }
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductid() {
		return productid;
	}
    
	public void setProductid(String productid) {
		this.productid = productid;
	}
	public void setorderstatus(String orderstatus) {
		this.orderstatus=orderstatus;
	}
	public String getorderstatus() {
		return orderstatus;
	}
	public void setProname(String name) {
		// TODO Auto-generated method stub
		this.productname=name;
	}
    public String getproname() {
    	return productname;
    }
	public void setPrice(double price) {
		// TODO Auto-generated method stub
		this.price=price;
	}
    public double getprice() {
    	return price;
    }
	public void setPaymentMode(String payment) {
		// TODO Auto-generated method stub
		this.paymentmode=payment;
	}
    public String getpaymentmode() {
    	return paymentmode;
    }
	public void setaddress(String address) {
		this.address=address;
	}
	public String getaddress() {
		return address;
	}
	public void setpaymentstatus(String paymentstatus) {
		this.paymentstatus=paymentstatus;
	}
	public String getpaymentstatus() {
		return paymentstatus;
	}

	@Override
	public String toString() {
		return "Orders [id=" + id + ", productid=" + productid + ", userId=" + userId + ", orderid=" + orderid
				+ ", orderstatus=" + orderstatus + ", productname=" + productname + ", price=" + price + ", address="
				+ address + ", paymentmode=" + paymentmode + ", paymentstatus=" + paymentstatus + ", feedback="
				+ feedback + ", rating=" + rating + "]";
	}

	
	
	
}
