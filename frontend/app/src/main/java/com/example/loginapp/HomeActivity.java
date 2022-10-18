package com.example.loginapp;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    // main menu activity with restaurants etc.
    private ImageButton imgBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView menuIcon = findViewById(R.id.menu_icon);
        TextView title = findViewById(R.id.restaurants_title);

        imgBtn = findViewById(R.id.idImgBtn);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // implement menu to open when clicking the menu icon
                Toast.makeText(HomeActivity.this, "Clicked menu", Toast.LENGTH_SHORT).show();
            }
        });

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are displaying a toast message.
                Toast.makeText(HomeActivity.this, "This is a image button", Toast.LENGTH_SHORT).show();
            }
        });
    }
}