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

    public String doPostRequest(String username, String password, String reqType) {
        Log.d("OKHTTP3", "POST function called");
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject data = new JSONObject();
        String url = "http://10.0.2.2:5000/api/users/" + reqType;
        try {
            data.put("username", username);
            data.put("password", password);
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
        Log.d("response iss:", newReq.toString());
        try {
            Response response = client.newCall(newReq).execute();
            Log.d("OKHTTP3", "Request done, got the response");
            Log.d("OKHTTP3", response.body().string());
            return response.toString();
        } catch (IOException e) {
            Log.d("OKHTTP3", "Exception while doing request.");
            e.printStackTrace();
            return "error in post call";
        }
    }
}