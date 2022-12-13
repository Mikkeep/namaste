package com.example.namaste;


import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import kotlin.collections.ArrayDeque;
import okhttp3.Response;

public class RestaurantActivity extends AppCompatActivity {
    /*
      Gets the restaurant items information from MainActivity and uses these to create buttons
      for each item that the restaurant has.

      Clicking on one of the buttons sends a POST request to /api/restaurants/orders in addition to this
      the user's session ID is saved to external memory on the device and can be changed from there to
      search for orders from other users or use the admin id to find all orders.

      The post request contains a JSON in format:
      {
        "rest_id": <restaurant_id>,
        "item_id": <item_id> (currently always 1 because there is only 1 item per restaurant),
        "amount": <amount> (currently 1 because you can only send one order at a time),
        "description": "Oulu"
      }
     */
    String sId;
    String rId;
    // click listener for the restaurant buttons
    private final View.OnClickListener menuButtonListener = (v) -> {
        // check which button was clicked
        OkHttpPostRequest postReq = new OkHttpPostRequest();
        Button btn = (Button) v;
        String btnId = String.valueOf(btn.getId());
        String msg = null;
        try {
            msg = new JSONObject()
                    .put("rest_id", rId)
                    .put("item_id", btnId)
                    .put("amount", "1")
                    .put("description", "Oulu")
                    .toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // userid, restaurantid, itemid, amount, location
        Log.d("data to be sent: ", rId + btnId + "1" + "Oulu" + sId);
        Response response = postReq.doPostRequest("restaurant/order/", msg, sId);

        if (response.toString().contains("200")) {
            // checks if internal storage is available for read/write
            if (isExternalStorageAvailableForRW()) {
                File file = new File(Environment.getExternalStorageDirectory() + "/Documents", "orders.txt");
                String fileContent = "sessionID: " + sId + "\n";
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    fos.write(fileContent.getBytes(StandardCharsets.UTF_8));
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(RestaurantActivity.this, "Order sent to cart!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RestaurantActivity.this, "Error in order.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        // gets the information required for POST from the bundle that is sent from MainActivity
        Integer id = getIntent().getIntExtra("id", 0);
        String products = getIntent().getStringExtra("products");
        String userId = getIntent().getStringExtra("userId");
        sId = userId;
        rId = id.toString();

        products = products.replace("[", "");
        products = products.replace("]", "");
        products = products.replace("\"", "");

        List<String> productList = new ArrayDeque<String>(Arrays.asList(products.split(",")));

        LinearLayout menuBoard = findViewById(R.id.menuLayout);
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(1000, 300);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // creates buttons from the items that the restaurant has
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).contains("id:")) {
                continue;
            }
            Log.d("item id", String.valueOf(i));
            Button btn = new Button(this);
            btn.setId(i + 1);
            btn.setText(productList.get(i).replace("{name:", ""));
            btn.setTextSize(35);
            btn.setLayoutParams(mp);
            btn.setOnClickListener(menuButtonListener);
            menuBoard.addView(btn);
        }
    }

    private boolean isExternalStorageAvailableForRW() {
        String extStorageState = Environment.getExternalStorageState();
        return extStorageState.equals((Environment.MEDIA_MOUNTED));
    }
}
