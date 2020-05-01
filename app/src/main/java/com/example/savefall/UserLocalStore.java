package com.example.savefall;

import android.content.Context;
import android.content.SharedPreferences;

public class UserLocalStore {
    // for database on phone
    public  static  final String DATA_STORE_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    // Instatiate userLocalDatabase on phone
    public UserLocalStore(Context context){
        userLocalDatabase = context.getSharedPreferences(DATA_STORE_NAME, 0);
    }

    // write user date in local database on phone
    public void storeUserData(User user){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
//        spEditor.putString("name", user.name);
//        spEditor.putInt("age", user.age);
        spEditor.putString("login", user.login);
        spEditor.putString("password", user.password);
        spEditor.commit();
    }

    // get logged user date from database
    public User getLoggedUser(){
//        String name = userLocalDatabase.getString("name","");
//        int age = userLocalDatabase.getInt("age", -1);
        String login = userLocalDatabase.getString("login","");
        String password = userLocalDatabase.getString("password","");

        User loggedUser = new User( login, password);

        return loggedUser;
    }

    // set user logged in and write userLogged status in local database
    public void setUserLogged(boolean userLogged){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("userLogged", userLogged);
        spEditor.commit();
    }

    // check if user is logged in
    public boolean getUserLogged(){
        if(userLocalDatabase.getBoolean("userLogged", false) == true){
            return true;
        }
        else{
            return false;
        }
    }

    // clear user data if the user logged out
    public void clearLoggedUserData(){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

}
