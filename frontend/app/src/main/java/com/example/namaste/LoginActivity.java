package com.example.namaste;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView username = findViewById(R.id.username);
        TextView password = findViewById(R.id.password);

        Button loginButton = findViewById(R.id.loginBtn);
        Button registerButton = findViewById(R.id.registerBtn);

        // Show plain action bar with only app name
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.show();

        // Detect rooted phone by checking superuser binaries from the phone
        if (rootPhone()) {
            Toast.makeText(LoginActivity.this, "Rooting detected!", Toast.LENGTH_SHORT).show();
            // end activity id phone is rooted
            this.finish();
        } else
            Toast.makeText(LoginActivity.this, "No rooting detected", Toast.LENGTH_SHORT).show();

        try {
            ActivityInfo values = LoginActivity.this.getPackageManager().getActivityInfo(
                    this.getComponentName(), 0);
            if ((values.flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0) {
                Toast.makeText(LoginActivity.this, "Hardware accelerated", Toast.LENGTH_SHORT).show();
                // Detects if an emulator is used and closes activity if one is detected
                EmulatorDetection isEmulator = new EmulatorDetection();
                if (isEmulator.inEmulator()) {
                    Toast.makeText(LoginActivity.this, "Emulator detected!", Toast.LENGTH_SHORT).show();
                    // end the activity if emulator detected with hardware acceleration turned on
                    this.finish();
                } else
                    Toast.makeText(LoginActivity.this, "No emulator detected", Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        
        loginButton.setOnClickListener(view -> {
            OkHttpPostRequest postReq = new OkHttpPostRequest();

            String msg = null;
            try {
                msg = new JSONObject()
                        .put("username", username.getText().toString())
                        .put("password", password.getText().toString())
                        .toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Response response = postReq.doPostRequest("users/login", msg, null);
            if (response != null) {
                if (response.toString().contains("200")) {
                    Toast.makeText(LoginActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    intent.putExtra("EXTRA_SESSION_ID", response.headers().get("Set-Cookie"));
                    String sUsername;
                    try {
                        JSONObject bodyJson = new JSONObject(response.body().string());
                        sUsername = bodyJson.getString("name");
                        // for some reason backend mixes up booleans and integers in admin values so we check for both
                        boolean sIsAdmin;
                        try {
                            sIsAdmin = (bodyJson.getInt("is_admin") == 1);
                        } catch (JSONException e) {
                            sIsAdmin = bodyJson.getBoolean("is_admin");
                        }

                        intent.putExtra("IS_ADMIN", sIsAdmin);
                        intent.putExtra("USERNAME", sUsername);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("intent extras", intent.getExtras().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "login failed.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Backend not responding...", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(view -> {
            // Intent into a register page
            Intent intent = new Intent(view.getContext(), RegisterActivity.class);
            startActivity(intent);
        });
    }

    public boolean rootPhone() {
        String[] superUserBinaries = new String[13];
        superUserBinaries[0] = "/system/app/Superuser/Superuser.apk";
        superUserBinaries[1] = "/system/app/Superuser.apk";
        superUserBinaries[2] = "/sbin/su";
        superUserBinaries[3] = "/system/bin/su";
        superUserBinaries[4] = "/system/xbin/su";
        superUserBinaries[5] = "/data/local/xbin/su";
        superUserBinaries[6] = "/data/local/bin/su";
        superUserBinaries[7] = "/system/sd/xbin/su";
        superUserBinaries[8] = "/system/bin/failsafe/su";
        superUserBinaries[9] = "/data/local/su";
        superUserBinaries[10] = "/su/bin/su";
        superUserBinaries[11] = "re.robv.android.xposed.installer-1.apk";
        superUserBinaries[12] = "/data/app/eu.chainfire.supersu-1/base.apk";
        File root;
        for (String superUserBinary : superUserBinaries) {
            root = new File(superUserBinary);
            if (root.exists()) {
                return true;
            }
        }
        return false;
    }
}