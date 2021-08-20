package com.example.food.Common;

import com.example.food.AdminSide.AddNewFood.SizeAvailable;

import java.util.List;

public class Food_Constructor {

    private String menuId,name,image,description,price,discount;
    private List<SizeAvailable> Options;

    public Food_Constructor(){
    }

    public Food_Constructor(String menuId , String name, String image, String description, List<SizeAvailable> options, String price, String discount) {
        this.menuId = menuId;
        this.name = name;
        this.image = image;
        this.description = description;
        this.Options = options;
        this.price = price;
        this.discount = discount;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SizeAvailable> getOptions() {
        return Options;
    }

    public void setOptions(List<SizeAvailable> options) {
        Options = options;
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
