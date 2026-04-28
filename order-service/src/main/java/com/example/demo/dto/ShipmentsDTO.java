package com.example.demo.dto;

public class ShipmentsDTO {
	 private String orderid;
	    private String carriername;
	    private String shippingAddress; // New field from Order Service
	    private String paymentstatus;
	    
	    public ShipmentsDTO() {
	    	
	    }
public ShipmentsDTO(String orderid,String carriername,String shippingAddress,String paymentstatus) {
	    	this.orderid=orderid;
	    	this.carriername=carriername;
	    	this.shippingAddress=shippingAddress;
	    	this.paymentstatus=paymentstatus;
	    }
public String getOrderid() {
	return orderid;
}
public void setOrderid(String orderid) {
	this.orderid = orderid;
}
public String getCarriername() {
	return carriername;
}
public void setCarriername(String carriername) {
	this.carriername = carriername;
}
public String getShippingAddress() {
	return shippingAddress;
}
public void setShippingAddress(String shippingAddress) {
	this.shippingAddress = shippingAddress;
}
public String getPaymentstatus() {
	return paymentstatus;
}
public void setPaymentstatus(String paymentstatus) {
	this.paymentstatus = paymentstatus;
}
@Override
public String toString() {
	return "ShipmentsDTO [orderid=" + orderid + ", carriername=" + carriername + ", shippingAddress=" + shippingAddress
			+ ", paymentstatus=" + paymentstatus + "]";
}  
	    
}
