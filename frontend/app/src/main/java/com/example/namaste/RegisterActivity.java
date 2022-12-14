package com.example.namaste;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity {
    // activity for registering users

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView username = findViewById(R.id.reg_username);
        TextView password = findViewById(R.id.reg_password);

        Button registerButton = findViewById(R.id.regBtn);
        Button cancelButton = findViewById(R.id.regCancel);

        // Show plain action bar with only app name
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.show();

        registerButton.setOnClickListener(view -> {
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
            Response response = postReq.doPostRequest("users/register/", msg, null);
            Log.d("response was: ", response.toString());
            if (response.toString().contains("200")) {
                Toast.makeText(RegisterActivity.this, "Register successful!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "register failed", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(view -> finish());
    }
}
