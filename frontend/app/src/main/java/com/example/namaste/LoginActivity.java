package com.example.namaste;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
<<<<<<< HEAD
=======

import android.content.Context;
import android.content.SharedPreferences;
>>>>>>> main
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

<<<<<<< HEAD
=======
import okhttp3.Response;
import okhttp3.internal.http2.Header;

>>>>>>> main
public class LoginActivity extends AppCompatActivity {
    // login activity
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String name = "nameKey";

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
            String msg = String.format("{\r\n    \"username\": \"%s\",\r\n    \"password\": \"%s\"\r\n}", username.getText().toString(), password.getText().toString());
            Log.d("message content: ", msg);
<<<<<<< HEAD
            String response = postReq.doPostRequest(username.getText().toString(), password.getText().toString(), "login");
            Log.d("response was: ", response);
            if(response.contains("200")) {
                Toast.makeText(LoginActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
=======
            Response response = postReq.doPostRequest(username.getText().toString(), password.getText().toString(), "login");
            //Log.d("got here", "got here!");
            if(response.toString().contains("200")) {
                Toast.makeText(LoginActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("EXTRA_SESSION_ID", response.headers().get("Set-Cookie"));
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "login failed.", Toast.LENGTH_SHORT).show();
>>>>>>> main
            }
        });

        registerButton.setOnClickListener(view -> {
            // Intent into a register page
            Intent intent = new Intent(view.getContext(), RegisterActivity.class);
            startActivity(intent);
        });
    }

}