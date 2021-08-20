package com.example.food.AdminSide.EmployeesDetails;

public class Employees {

    private String EmployeeName,Number,Age,Address;

    public Employees(){}

    public Employees(String employeeName, String number, String age, String address) {
        EmployeeName = employeeName;
        Number = number;
        Age = age;
        Address = address;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
