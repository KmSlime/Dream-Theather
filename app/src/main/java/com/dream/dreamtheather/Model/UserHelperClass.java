package com.dream.dreamtheather.Model;

public class UserHelperClass {

    String email, userName, passWord, phoneNumber;

    public UserHelperClass() {
    }

    public UserHelperClass(String email, String userName, String passWord, String phoneNumber) {
        this.email = email;
        this.userName = userName;
        this.passWord = passWord;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
