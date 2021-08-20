package com.example.food.Common;

public class UserDetails_Constructor {

    String USER_ID,fullName, email, phoneNo, password, date, gender,Isstaff,Address,DLatitude,DLongitude,Token;

    public UserDetails_Constructor(){}

    //Getters
    public UserDetails_Constructor(String USER_ID, String fullName, String email, String phoneNo, String password, String date, String gender, String  Isstaff, String Address, String DLatitude, String DLongitude, String Token) {
        this.USER_ID = USER_ID;
        this.fullName = fullName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.password = password;
        this.date = date;
        this.gender = gender;
        this.Isstaff = Isstaff;
        this.Address = Address;
        this.DLatitude = DLatitude;
        this.DLongitude = DLongitude;
        this.Token = Token;
    }


    public String getIsstaff() {
        return Isstaff;
    }

    public void setIsstaff(String isstaff) {
        Isstaff = isstaff;
    }

    //Get and Set the Value from generators
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDLatitude() {
        return DLatitude;
    }

    public void setDLatitude(String DLatitude) {
        this.DLatitude = DLatitude;
    }

    public String getDLongitude() {
        return DLongitude;
    }

    public void setDLongitude(String DLongitude) {
        this.DLongitude = DLongitude;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
