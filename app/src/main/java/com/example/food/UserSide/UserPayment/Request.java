package com.example.food.UserSide.UserPayment;

import com.example.food.UserSide.Order_Constructor;

import java.util.List;

public class Request {

    private String adminPhone;
    private String name;
    private String address;
    private String total;
    private String Mode;
    private List<Order_Constructor> foods;
    private List<Coordinates> coordinates;
    private String Status;
    private String token;


    public Request(){
    }

    public Request(String adminPhone, String name, String address, String total, String Mode, String Status , List<Order_Constructor> foods, List<Coordinates> coordinates, String token) {
        this.adminPhone = adminPhone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.Mode = Mode;
        this.Status = Status;
        this.foods = foods;
        this.coordinates = coordinates;
        this.token = token;
    }

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }


    public String getAdminPhone() {
        return adminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order_Constructor> getFoods() {
        return foods;
    }

    public void setFoods(List<Order_Constructor> foods) {
        this.foods = foods;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public List<Coordinates> getCoordinates() {
        return coordinates;
    }
    public void setCoordinates(List<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

