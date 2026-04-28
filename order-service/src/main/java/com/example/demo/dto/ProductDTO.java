package com.example.demo.dto;

public class ProductDTO {
	
	private String productId;
    private String name;
    private double price;
    private int qunt;
	public String address;
    public ProductDTO() {
    	
    }
public ProductDTO(String productId,String name,double price,int qunt,String address) {
    	this.productId=productId;
    	this.name=name;
    	this.price=price;
    	this.qunt=qunt;
    	this.address=address;
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
public double getPrice() {
	return price;
}
public void setPrice(double price) {
	this.price = price;
}
public int getQunt() {
	return qunt;
}
public void setQunt(int qunt) {
	this.qunt = qunt;
}
public void setaddress(String address) {
	this.address=address;
}
public String getadress() {
	return address;
}
@Override
public String toString() {
	return "ProductDTO [productId=" + productId + ", name=" + name + ", price=" + price + ", qunt=" + qunt +",address="+address+"]";
}

}
