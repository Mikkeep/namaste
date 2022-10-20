package com.example.namaste;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpPostRequest {

    public void doPostRequest(String username, String password) {
        Log.d("OKHTTP3", "POST function called");
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject data = new JSONObject();
        String url = "http://127.0.0.1:5000/api/users/login/";
        try {
            data.put("username", "admin");
            data.put("password", "supersecurepassword123456");
        } catch (JSONException e) {
            Log.d("OKHTTP3", "JSON exception");
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, data.toString());
        Log.d("OKHTTP3", "Request body created.");
        Request newReq = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(newReq).execute();
            Log.d("OKHTTP3", "Request done, got the response");
            Log.d("OKHTTP3", response.body().string());
        } catch (IOException e) {
            Log.d("OKHTTP3", "Exception while doing request.");
            e.printStackTrace();
        }

    }
}