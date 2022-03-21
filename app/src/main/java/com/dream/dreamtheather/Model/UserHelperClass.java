package com.dream.dreamtheather.Model;

public class UserHelperClass {

    String Email, Username, Password;

    public UserHelperClass() {
    }

    public UserHelperClass(String email, String username, String password) {
        Email = email;
        Username = username;
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
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
