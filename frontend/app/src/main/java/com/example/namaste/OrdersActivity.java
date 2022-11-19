package com.example.namaste;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(R.string.sub_orders);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}