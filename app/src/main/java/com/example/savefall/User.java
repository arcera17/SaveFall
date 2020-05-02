package com.example.savefall;

public class User {
    String name,  login, password, email, privacyPolicy ,birthDate;
    int id;

    public User(int id, String login, String password, String name, String email,String privacyPolicy, String birthDate){
        this.name = name;
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
        this.privacyPolicy = privacyPolicy;
        this.birthDate = birthDate;
    }

    public User(String login, String password){
        this.login = login;
        this.password = password;
    }
}
