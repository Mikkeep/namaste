package com.example.namaste;

import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpGetRequest {
    public Response doGetRequest(String cookie, String reqType) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        String url = "http://10.0.2.2:5000/api/" + reqType;
        Log.d("OKHTTP3", "GET function called");
        OkHttpClient client = new OkHttpClient();

        Response response = null;

        if (reqType.contains("restaurant/all")) {
            Request newReq = new Request.Builder()
                    .addHeader("Cookie", "session=" + cookie)
                    .url(url)
                    .build();
            try {
                response = client.newCall(newReq).execute();
                Log.d("OKHTTP3", "Request done, got the response");
            } catch (IOException e) {
                Log.d("OKHTTP3", "Exception while doing request.");
                e.printStackTrace();
            }
        }

        if (reqType.contains("file")) {
            Request newReq = new Request.Builder()
                    .addHeader("Cookie", "session=" + cookie)
                    .url(url)
                    .build();
            try {
                response = client.newCall(newReq).execute();
                Log.d("OKHTTP3", "Request done, got the response");
            } catch (IOException e) {
                Log.d("OKHTTP3", "Exception while doing request.");
                e.printStackTrace();
            }
        }


        return response;
    }
}
