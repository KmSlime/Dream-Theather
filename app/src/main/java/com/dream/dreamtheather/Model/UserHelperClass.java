package com.dream.dreamtheather.Model;

public class UserHelperClass {

    String Username, Password;

    public UserHelperClass() {
    }

    public UserHelperClass(String username, String password) {
        Username = username;
        Password = password;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
