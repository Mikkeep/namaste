package com.example.namaste;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import java.util.Arrays;
import java.util.List;

import kotlin.collections.ArrayDeque;
import okhttp3.Response;

public class RestaurantActivity extends AppCompatActivity {
    String sId;
    String rId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("rest page", "got to restaurant page");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        Integer id = getIntent().getIntExtra("id", 0);
        String products = getIntent().getStringExtra("products");
        String userId = getIntent().getStringExtra("userId");
        sId = userId;
        rId = id.toString();

        products = products.replace("[","");
        products = products.replace("]","");
        products = products.replace("\"","");

        List<String> productList = new ArrayDeque<String>(Arrays.asList(products.split(",")));

        Log.d("id in rest page", id.toString());
        //Log.d("name in rest page", name);
        Log.d("products in rest page", products);

        LinearLayout menuBoard = findViewById(R.id.menuLayout);
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(1000, 300);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        for(int i = 0; i < productList.size(); i++) {
            Button btn = new Button(this);
            btn.setId(i+1);
            btn.setText(productList.get(i));
            btn.setTextSize(40);
            btn.setLayoutParams(mp);
            btn.setOnClickListener(menuButtonListener);
            menuBoard.addView(btn);
        }
    }

    // click listener for the restaurant buttons
    private final View.OnClickListener menuButtonListener = (v) -> {
        // check which button was clicked
        OkHttpPostRequest postReq = new OkHttpPostRequest();
        Button btn = (Button) v;
        String btnId = String.valueOf(btn.getId());
        // userid, restaurantid, itemid, amount, location
        Response response = postReq.doPostRequest(sId, rId, btnId, "1", "Oulu", "/restaurant/order/");
        if(response.toString().contains("200")) {
            Toast.makeText(RestaurantActivity.this, "Order sent to cart!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RestaurantActivity.this, "Error in order.", Toast.LENGTH_SHORT).show();
        }
    };
}
