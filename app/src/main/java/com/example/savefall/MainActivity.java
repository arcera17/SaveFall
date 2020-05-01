package com.example.savefall;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button homeButton, loginButton, registrationButton;

    //local database
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Open activity_main
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // To home button action on click
        homeButton = (Button) findViewById(R.id.to_home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomePage();
            }
        });

        // To registration Page
        registrationButton = (Button) findViewById(R.id.registrationButton);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegPage();
            }
        });

        // To login Page
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginPage();
            }
        });

        userLocalStore = new UserLocalStore(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(authentication() == true){
            openHomePage();
        }
    }

    // check authentication
    private boolean authentication(){
        return userLocalStore.getUserLogged();
    }

    // Change page to activity_home_page
    public void openHomePage(){
        Intent home = new Intent(this, HomePage.class );
        startActivity(home);
    }

    // Change page to activity_registration_page
    public void openRegPage(){
        Intent register = new Intent(this, RegistrationPage.class);
        startActivity(register);
    }

    // Change page to activity_login_page
    public void openLoginPage(){
        Intent login = new Intent(this, LoginPage.class);
        startActivity(login);
    }
}
