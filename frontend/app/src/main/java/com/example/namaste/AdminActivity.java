package com.example.namaste;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setSubtitle(R.string.sub_admin);
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView adminPass = findViewById(R.id.re_enter_admin_field);
        Button adminBtn = findViewById(R.id.adminBtn);
        adminBtn.setOnClickListener(view -> {
            if (adminPass.getText().toString().equals("supersecurepassword123456")) {
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.activity_admin_panel);
            } else {
                Toast.makeText(this, "Invalid admin password", Toast.LENGTH_SHORT).show();
            }
            }
        );
    }
}