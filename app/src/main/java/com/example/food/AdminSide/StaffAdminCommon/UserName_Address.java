package com.example.food.AdminSide.StaffAdminCommon;

public class UserName_Address {
    private String name;
    private String address;
    private String adminPhone;
    private String OrderId;

    public UserName_Address() {
    }

    public UserName_Address(String ID,String name,String adminPhone ,String address) {
        this.OrderId = ID;
        this.name = name;
        this.adminPhone = adminPhone;
        this.address = address;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
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

    public String getAdminPhone() {
        return adminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }
}
