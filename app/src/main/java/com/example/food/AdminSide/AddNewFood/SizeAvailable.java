package com.example.food.AdminSide.AddNewFood;

public class SizeAvailable {

    private String SizeCategory,Quantity,Price,Discount;

    public SizeAvailable(){}

    public SizeAvailable(String sizeCategory, String quantity, String price, String discount) {
        SizeCategory = sizeCategory;
        Quantity = quantity;
        Price = price;
        Discount = discount;
    }

    public String getSizeCategory() {
        return SizeCategory;
    }

    public void setSizeCategory(String sizeCategory) {
        SizeCategory = sizeCategory;
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

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
