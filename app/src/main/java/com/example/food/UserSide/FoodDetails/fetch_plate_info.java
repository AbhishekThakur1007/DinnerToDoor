package com.example.food.UserSide.FoodDetails;

public class fetch_plate_info {
    String quantity;
    String sizeCategory;
    String price;
    String discount;

    public fetch_plate_info(){}

    public fetch_plate_info(String quantity, String sizeCategory, String price, String discount) {
        this.quantity = quantity;
        this.sizeCategory = sizeCategory;
        this.price = price;
        this.discount = discount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSizeCategory() {
        return sizeCategory;
    }

    public void setSizeCategory(String sizeCategory) {
        this.sizeCategory = sizeCategory;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
