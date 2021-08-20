package com.example.food.AdminSide.StaffAdminCommon;

public class OrderDetailsConstructor {
    private String adminPhone;
    private String name;
    private String address;
    private String total;
    private String Mode;
    private String Status;

    public OrderDetailsConstructor() {
    }

    public OrderDetailsConstructor(String adminPhone, String name, String address, String total, String mode, String status) {
        this.adminPhone = adminPhone;
        this.name = name;
        this.address = address;
        this.total = total;
        Mode = mode;
        Status = status;
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

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
