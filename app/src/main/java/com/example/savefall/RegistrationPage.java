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
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegistrationPage extends AppCompatActivity {

    private Button registerBtn;
    private EditText loginInput,  passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        registerBtn = (Button) findViewById(R.id.registerButton);

        // register on click
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    // register new user
    private void register(){
        // for Http parameters
        String url = HomePage.appURL +  "/api/register.php";
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
        url += urlParams(url, params);

        // create client for Http request and to request URL
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationPage.this);
                builder.setMessage("Http: bad" + e.getMessage())
                        .setNegativeButton("Retry", null)
                        .create()
                        .show();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(response.isSuccessful()){
                    final String myResponse = response.body().string();

                    RegistrationPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonResponse = new JSONObject(myResponse);
                                boolean success = jsonResponse.getBoolean("success");

                                // if is success registration
                                if(success){
                                    Intent loginPage = new Intent( RegistrationPage.this, LoginPage.class);
                                    startActivity(loginPage);
                                }
                                else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationPage.this);
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

        // create user
        User registerUserData = new User(login, password);
    }

    // add params to url
    public static String urlParams(String url , HashMap<String, String> params ) {
        Character and = new Character('&');
        Character lastChar = new Character(url.charAt(url.length()-1));

        StringBuilder sb = new StringBuilder();

        Iterator<?> iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            if (sb.length() <= 0) {
                sb.append('?');
            }
            else{
                sb.append('&');
            }
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();

            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        return sb.toString();
    }
}
