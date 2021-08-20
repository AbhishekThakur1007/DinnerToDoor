package com.example.food.Common.Cart;

public class nameQuantity_Constructor {
    String productName,quantity,Plate;

    public nameQuantity_Constructor(){}

    public nameQuantity_Constructor(String productName, String quantity, String plate) {
        this.productName = productName;
        this.quantity = quantity;
        this.Plate = plate;
    }

    public String getProductName() {
        return productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPlate() {
        return Plate;
    }

}
