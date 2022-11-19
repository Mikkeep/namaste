package com.example.namaste;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView username = findViewById(R.id.username);
        TextView password = findViewById(R.id.password);

        Button loginButton = findViewById(R.id.loginBtn);
        Button registerButton = findViewById(R.id.registerBtn);

        // Show plain action bar with only app name
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.show();

        loginButton.setOnClickListener(view -> {
            OkHttpPostRequest postReq = new OkHttpPostRequest();
            String msg = null;
            try {
                msg = new JSONObject()
                        .put("username", username.getText().toString())
                        .put("password", password.getText().toString())
                        .toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Response response = postReq.doPostRequest("users/login", msg, null);

            if (response != null) {
                if (response.toString().contains("200")) {
                    Toast.makeText(LoginActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    intent.putExtra("EXTRA_SESSION_ID", response.headers().get("Set-Cookie"));
                    String sUsername;
                    boolean sIsAdmin;

                    try {
                        JSONObject bodyJson = new JSONObject(Objects.requireNonNull(response.body()).string());
                        sUsername = bodyJson.getString("name");
                        //sIsAdmin = bodyJson.getBoolean("isAdmin"); // !!! REMOVE THIS LINE AFTER PROPER IMPLEMENTATION
                        boolean temp = username.getText().toString().equals("Admin");
                        intent.putExtra("IS_ADMIN", temp);
                        intent.putExtra("USERNAME", sUsername);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("intent extras", intent.getExtras().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "login failed.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Backend not responding...", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(view -> {
            // Intent into a register page
            Intent intent = new Intent(view.getContext(), RegisterActivity.class);
            startActivity(intent);
        });
    }

}