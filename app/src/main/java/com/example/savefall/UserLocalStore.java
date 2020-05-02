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
        spEditor.putInt("id", user.id);
        spEditor.putString("login", user.login);
        spEditor.putString("password", user.password);
        spEditor.putString("name", user.name);
        spEditor.putString("email", user.email);
        spEditor.putString("privacyPolicy", user.privacyPolicy);
        spEditor.putString("birthDate", user.birthDate);

        spEditor.commit();
    }

    // get logged user date from database
    public User getLoggedUser(){
        int id = userLocalDatabase.getInt("id", 0);
        String login = userLocalDatabase.getString("login","");
        String password = userLocalDatabase.getString("password","");
        String name = userLocalDatabase.getString("name","");
        String email = userLocalDatabase.getString("email","");
        String privacyPolicy = userLocalDatabase.getString("privacyPolicy", "");
        String birthDate = userLocalDatabase.getString("birthDate", "");
        User loggedUser = new User( id, login, password, name, email, privacyPolicy, birthDate);

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
