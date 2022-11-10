package com.example.namaste;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpPostRequest {

    public Response doPostRequest(String one, String two, String three, String four, String five, String reqType) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d("OKHTTP3", "POST function called");
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject data = new JSONObject();
        String url = "http://10.0.2.2:5000/api/" + reqType;

        if(reqType.contains("users/login") || reqType.contains("users/register")) {
            try {
                data.put("username", one);
                data.put("password", two);
            } catch (JSONException e) {
                Log.d("OKHTTP3", "JSON exception in login.");
                e.printStackTrace();
            }
        }

        if(reqType.contains("order")) {
            try {
                data.put("rest_id", one);
                data.put("item_id", two);
                data.put("amount", three);
                data.put("description", four);
            } catch (JSONException e) {
                Log.d("OKHTTP3", "JSON exception in order.");
                e.printStackTrace();
            }
        }

        Log.d("data to be posted:", data.toString());

        RequestBody body = RequestBody.create(JSON, data.toString());
        Log.d("OKHTTP3", "Request body created.");
        Log.d("OKHTTP3 RB:", body.toString());

        Request newReq = null;
        // Assemble headers and body for login/register request
        if(reqType.contains("users/login") || reqType.contains("users/register")) {
            newReq = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        }
        // Assemble headers and body for logout request
        if(reqType.contains("users/logout")) {
            newReq = new Request.Builder()
                    .addHeader("Cookie", "session=" + five)
                    .url(url)
                    .post(body)
                    .build();
        }
        // Assemble headers and body for order request
        if(reqType.contains("order")) {
            newReq = new Request.Builder()
                    .addHeader("Cookie", "session=" + five)
                    .url(url)
                    .post(body)
                    .build();
        }

        Log.d("response is:", newReq.toString());
        Response response = null;
        try {
            response = client.newCall(newReq).execute();
            Log.d("OKHTTP3", "Request done, got the response");
            if(response.toString().contains("200")) {
                Log.d("got here", "got here!");
                if(reqType.contains("users")) {
                    Headers headers = response.headers();
                    String sId = headers.get("Set-Cookie");
                    Log.d("Set-cookie: ", sId);
                }
            }
        } catch (IOException e) {
            Log.d("OKHTTP3", "Exception while doing request.");
            e.printStackTrace();
        }
        Log.d("OKHTTP3", String.valueOf(response));
        return response;
    }
}