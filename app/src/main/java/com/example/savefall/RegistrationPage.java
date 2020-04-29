package com.example.savefall;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private EditText emailInput,  passwordInput;
    private TextView testEmail, testPassword, requestTest, responseTest;

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

    // Register new user
    private void register(){
        // for Http parameters
        String url = "http://167.71.59.142/api/register.php";
        HashMap<String, String> params = new HashMap<String, String>();

        
        emailInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        testEmail = (TextView) findViewById(R.id.testEmail);
        testPassword = (TextView) findViewById(R.id.testPassword);
        requestTest = (TextView) findViewById(R.id.requestTest);
        responseTest = (TextView) findViewById(R.id.responseTest);

        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        testEmail.setText(email);
        testPassword.setText((password));

        // Paramds for url
        params.put("email", email);
        params.put("password", password);

        // Add params to url
        url += urlParams(url, params);

        // Create client for Http request and to request URL
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        requestTest.setText(request.toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseTest.setText("Http: bad" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String myResponse = response.body().toString();

                    RegistrationPage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            responseTest.setText("Response: "+myResponse);
                        }
                    });
                }
            }
        });
    }

    // Add params to url
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
