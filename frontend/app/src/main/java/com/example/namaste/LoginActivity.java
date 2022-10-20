package com.example.namaste;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;


import com.google.android.material.button.MaterialButton;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    // login activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        TextView username = findViewById(R.id.username);
        TextView password = findViewById(R.id.password);

        Button loginButton = (MaterialButton) findViewById(R.id.loginBtn);
        Button registerButton = (MaterialButton) findViewById(R.id.registerBtn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = String.format("{\r\n    \"username\": \"%s\",\r\n    \"password\": \"%s\"\r\n}", username.getText().toString(), password.getText().toString());
                Log.d("message content: ", msg);
                String response = doPostRequest(username.getText().toString(), password.getText().toString());
                Log.d("response was: ", response);
                if(response.contains("200")) {
                    Toast.makeText(LoginActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Intent into a register page
                Intent intent = new Intent(view.getContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    // does the post request through emulators IP to 127.0.0.1
    public static String doPostRequest(String username, String password) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        String msg = String.format("{\r\n    \"username\": \"%s\",\r\n    \"password\": \"%s\"\r\n}", username, password);
        RequestBody body = RequestBody.create(mediaType, msg);
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/api/users/login/")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "POST REQUEST ERROR";
    }
}