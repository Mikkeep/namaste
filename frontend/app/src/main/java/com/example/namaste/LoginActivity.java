package com.example.namaste;


import androidx.appcompat.app.ActionBar;
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

        TextView username = findViewById(R.id.username);
        TextView password = findViewById(R.id.password);

        Button loginButton = (MaterialButton) findViewById(R.id.loginBtn);
        Button registerButton = (MaterialButton) findViewById(R.id.registerBtn);

        // Show plain action bar with only app name
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.show();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpPostRequest postReq = new OkHttpPostRequest();
                String msg = String.format("{\r\n    \"username\": \"%s\",\r\n    \"password\": \"%s\"\r\n}", username.getText().toString(), password.getText().toString());
                Log.d("message content: ", msg);
                String response = postReq.doPostRequest(username.getText().toString(), password.getText().toString(), "login");
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

}