package com.example.savefall;

public class User {
    String name,  login, password;
    int age;

    public User(String name, int age, String login, String password){
        this.name = name;
        this.age = age;
        this.login = login;
        this.password = password;
    }

    public User(String login, String password){
        this.login = login;
        this.password = password;
    }
}
