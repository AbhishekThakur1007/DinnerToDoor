package com.example.food.AdminSide.StaffAdminCommon;

public class DeliveryDetailsConstructor {

    private String DName,DPhone;

    public DeliveryDetailsConstructor() {
    }

    public DeliveryDetailsConstructor(String DName, String DPhone) {
        this.DName = DName;
        this.DPhone = DPhone;
    }

    public String getDName() {
        return DName;
    }

    public void setDName(String DName) {
        this.DName = DName;
    }

    public String getDPhone() {
        return DPhone;
    }

    public void setDPhone(String DPhone) {
        this.DPhone = DPhone;
    }
}
