package com.example.food.UserSide;

public class Order_Constructor {

    private String productId;
    private String productName;
    private String Quantity;
    private String Price;
    private String Total;
    private String Plate;

    public Order_Constructor(){
    }

    public Order_Constructor(String productId, String productName, String quantity, String price, String total, String plate) {
        this.productId = productId;
        this.productName = productName;
        this.Quantity = quantity;
        this.Price = price;
        this.Total = total;
        this.Plate = plate;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getPlate() {
        return Plate;
    }

    public void setPlate(String plate) {
        Plate = plate;
    }

}



