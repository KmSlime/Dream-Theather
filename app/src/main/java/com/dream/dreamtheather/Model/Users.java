package com.dream.dreamtheather.Model;

import java.util.ArrayList;

public class Users {

    private String fullName;
    private String address, avaUrl, email, phoneNumber, birthDay, gender, userType;
    private ArrayList<Integer> idTicket;
    private int balance, LoyaltyPoint;

    public Users() {
    }

    public Users(String fullName, String address, String avaUrl, String email, String phoneNumber, String birthDay, String gender, String userType, ArrayList<Integer> idTicket, int balance, int loyaltyPoint) {
        this.fullName = fullName;
        this.address = address;
        this.avaUrl = avaUrl;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDay = birthDay;
        this.gender = gender;
        this.userType = userType;
        this.idTicket = idTicket;
        this.balance = balance;
        LoyaltyPoint = loyaltyPoint;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvaUrl() {
        return avaUrl;
    }

    public void setAvaUrl(String avaUrl) {
        this.avaUrl = avaUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public ArrayList<Integer> getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(ArrayList<Integer> idTicket) {
        this.idTicket = idTicket;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getLoyaltyPoint() {
        return LoyaltyPoint;
    }

    public void setLoyaltyPoint(int loyaltyPoint) {
        LoyaltyPoint = loyaltyPoint;
    }
}
