package com.example.namaste;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class OrdersActivity extends AppCompatActivity {
    /*
   Uses the sessionID provided in the external memory that was saved in the previous activity.
   Posts this into /api/restaurant/order/history to get order information and credit card
   details from all the orders that the said sessionID has made.
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(R.string.sub_orders);
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView tvLoad = findViewById(R.id.tvLoad);

        // Initiate a file reader that targets the file in emulator's external storage
        FileReader fr = null;
        File file = new File(Environment.getExternalStorageDirectory() + "/Documents", "orders.txt");
        StringBuilder stringBuilder = new StringBuilder();
        try {
            fr = new FileReader(file);
            BufferedReader bufferReader = new BufferedReader(fr);
            String line = bufferReader.readLine();
            line = line.replace("sessionID: ", "");
            stringBuilder.append(line).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            String sId = String.valueOf(stringBuilder);
            sId = sId.replace("\n", "").replace("\r", "");

            // creating a null JSON body to send to the backend
            String msg = null;
            try {
                msg = new JSONObject()
                        .put("", "")
                        .toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("ordersSID: ", sId);
            OkHttpPostRequest postReq = new OkHttpPostRequest();
            Response response = postReq.doPostRequest("restaurant/order/history/", msg, sId);
            JSONObject json;

            // If response contains 200 = valid, it will create a list of orders in the orders page
            if (response.toString().contains("200")) {
                String responseData = null;
                try {
                    responseData = response.body().string();
                    json = new JSONObject(responseData);
                    Iterator<String> keys = json.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        if (json.get(key) instanceof JSONObject) {
                            tvLoad.setText(tvLoad.getText() +
                                    "\nOrder # " + key +
                                    "\n     Item: " + json.getJSONObject(key).getString("item_name") +
                                    "\n     Restaurant: " + json.getJSONObject(key).getString("rest_name") +
                                    "\n     Location: " + json.getJSONObject(key).getString("description") +
                                    "\n     CC: " + json.getJSONObject(key).getString("cardnumber") + "\n"
                            );
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}