package com.example.savefall;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginPage extends AppCompatActivity {

    private Button loginBtn;
    private EditText loginInput,  passwordInput;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        loginBtn = (Button) findViewById(R.id.loginButton);

        // register on click
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        userLocalStore = new UserLocalStore(this);
    }

    private void login(){
        // for Http parameters
        String url = HomePage.appURL + "/api/login.php";
        HashMap<String, String> params = new HashMap<String, String>();

        // data from activity
        loginInput = (EditText) findViewById(R.id.loginInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        // get entered data
        String login = loginInput.getText().toString();
        String password = passwordInput.getText().toString();

        // paramds for url
        params.put("login", login);
        params.put("password", password);

        // add params to url
        url += RegistrationPage.urlParams(url, params);

        // create client for Http request and to request URL
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginPage.this);
                builder.setMessage("Http: bad" + e.getMessage())
                        .setNegativeButton("Retry", null)
                        .create()
                        .show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String myResponse = response.body().string();

                    LoginPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonResponse = new JSONObject(myResponse);
                                boolean success = jsonResponse.getBoolean("success");

                                // if is success registration
                                if(success){
                                    int userId = jsonResponse.getInt("id_user");
                                    String login = jsonResponse.getString("login");
                                    String password = jsonResponse.getString("pass");
                                    String name = jsonResponse.getString("name");
                                    String email = jsonResponse.getString("email");
                                    String privacyPolicy = jsonResponse.getString("privacy_policy");
                                    String birthdate = jsonResponse.getString("birth_date");

                                    User  user = new User(userId, login, password, name, email, privacyPolicy, birthdate);

                                    userLocalStore.storeUserData(user);
                                    userLocalStore.setUserLogged(true);

                                    Intent homePage = new Intent( LoginPage.this, HomePage.class);
                                    startActivity(homePage);
                                }
                                else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginPage.this);
                                    builder.setMessage("Register failed, Please try again!")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

    }
}
