package com.example.demo.entity;

import java.util.Date;

import jakarta.persistence.*;

@Entity
public class Shipments {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
private long id;
	@Column
private String orderid;
	@Column
private String trackingid;
	@Column
private String carriername;
	@Column
private Date shippedat;
	@Column
private Date estimateddelivery;
	@Column
private String paymentstatus;
	@Column
	private String shippingaddress;
	@Column
	private String returnstatus;
	
	public Shipments() {
		
	}
public Shipments(long id,String orderid,String trackingid,String Carriername,Date shippedat,Date estimateddelivery,String paymentstatus,String shippingaddress,String returnstatus) {
this.id=id;
this.orderid=orderid;
this.trackingid=trackingid;
this.carriername=carriername;
this.shippedat=shippedat;
this.estimateddelivery=estimateddelivery;
this.paymentstatus=paymentstatus;
this.shippingaddress=shippingaddress;
this.returnstatus=returnstatus;
}
public void setreturnstatus(String returnstatus) {
	this.returnstatus=returnstatus;
}
public String getreturnstatus() {
	return returnstatus;
}
public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public String getOrderid() {
	return orderid;
}
public void setOrderid(String orderid) {
	this.orderid = orderid;
}
public String getTrackingid() {
	return trackingid;
}
public void setTrackingid(String trackingid) {
	this.trackingid = trackingid;
}
public String getCarriername() {
	return carriername;
}
public void setCarriername(String carriername) {
	this.carriername = carriername;
}
public Date getShippedat() {
	return shippedat;
}
public void setShippedat(Date shippedat) {
	this.shippedat = shippedat;
}
public Date getEstimateddelivery() {
	return estimateddelivery;
}
public void setEstimateddelivery(Date estimateddelivery) {
	this.estimateddelivery = estimateddelivery;
}
public String getPaymentstatus() {
	return paymentstatus;
}
public void setPaymentstatus(String paymentstatus) {
	this.paymentstatus = paymentstatus;
}
@Override
public String toString() {
	return "Shipments [id=" + id + ", orderid=" + orderid + ", trackingid=" + trackingid + ", carriername="
			+ carriername + ", shippedat=" + shippedat + ", estimateddelivery=" + estimateddelivery + ", paymentstatus="
			+ paymentstatus + ", shippingaddress=" + shippingaddress + ", returnstatus=" + returnstatus + "]";
}
public void setshippingaddress(String shippingaddress) {
	this.shippingaddress=shippingaddress;
}
public String getshippingaddress() {
	return shippingaddress;
}



}
