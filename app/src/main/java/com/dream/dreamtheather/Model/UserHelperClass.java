package com.dream.dreamtheather.Model;

public class UserHelperClass {

    String email, userName, passWord, phoneNumber, userDOB, User_Name, User_Address, User_Email ;
    int User_LoyaltyPoint;

    public UserHelperClass() {
    }

    public UserHelperClass(String email, String userName, String passWord, String phoneNumber, String userDOB, String user_Name, String user_Address, String user_Email, int user_LoyaltyPoint) {
        this.email = email;
        this.userName = userName;
        this.passWord = passWord;
        this.phoneNumber = phoneNumber;
        this.userDOB = userDOB;
        this.User_Name = user_Name;
        this.User_Address = user_Address;
        this.User_Email = user_Email;
        this.User_LoyaltyPoint = user_LoyaltyPoint;
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

    public String getUserDOB() {
        return userDOB;
    }

    public void setUserDOB(String userDOB) {
        this.userDOB = userDOB;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getUser_Address() {
        return User_Address;
    }

    public void setUser_Address(String user_Address) {
        User_Address = user_Address;
    }

    public String getUser_Email() {
        return User_Email;
    }

    public void setUser_Email(String user_Email) {
        User_Email = user_Email;
    }

    public int getUser_LoyaltyPoint() {
        return User_LoyaltyPoint;
    }

    public void setUser_LoyaltyPoint(int user_LoyaltyPoint) {
        User_LoyaltyPoint = user_LoyaltyPoint;
    }
}
