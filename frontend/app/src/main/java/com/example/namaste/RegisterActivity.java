package com.example.namaste;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
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

        // Show plain action bar with only app name
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.show();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpPostRequest postReq = new OkHttpPostRequest();
                String msg = String.format("{\r\n    \"username\": \"%s\",\r\n    \"password\": \"%s\"\r\n}", username.getText().toString(), password.getText().toString());
                Log.d("message content: ", msg);
                String response = postReq.doPostRequest(username.getText().toString(), password.getText().toString(), "register");
                Log.d("response was: ", response);
                if(response.contains("200")) {
                    Toast.makeText(RegisterActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
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
