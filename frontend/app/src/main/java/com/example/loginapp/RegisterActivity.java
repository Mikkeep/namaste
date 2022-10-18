package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {
    // activity for registering users

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView username = findViewById(R.id.reg_username);
        TextView password = findViewById(R.id.reg_password);

        Button registerButton = (MaterialButton) findViewById(R.id.regBtn);
        Button cancelButton = (MaterialButton) findViewById(R.id.regCancel);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intents into login page if username and password match
                if(username.getText().toString().equals("admin") && password.getText().toString().equals("1234")){
                    Toast.makeText(RegisterActivity.this, "Username already exists!", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Create the query that posts the username and password into backend here
                    Toast.makeText(RegisterActivity.this, "Register successful!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
