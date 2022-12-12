package com.example.namaste;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_UNSPECIFIED;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Response;
import okhttp3.ResponseBody;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private static final int PERMISSION_REQUEST_CODE = 200;
    ArrayList<String> restNames = new ArrayList<>();
    ArrayList<String> restDesc = new ArrayList<>();
    ArrayList<JSONObject> restItems = new ArrayList<>();

    private String sId;
    private boolean sIsAdmin;
    private String sUsername;

    // click listener for the restaurant buttons
    private final View.OnClickListener restaurantButtonListener = (v) -> {
        // check which button was clicked
        Button btn = (Button) v;
        Integer id = btn.getId();
        Toast.makeText(MainActivity.this, "Clicked button " + btn.getId(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, RestaurantActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("name", btn.getText());
        intent.putExtra("userId", sId);
        Log.d("name of restaurant", btn.getText().toString());
        intent.putExtra("products", restItems.get(id - 1).toString());
        startActivity(intent);
    };

    @Override
    protected void onResume() {
        super.onResume();
        sIsAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        navigationView.getMenu().findItem(R.id.nav_admin).setVisible(sIsAdmin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // get username from extras set by LoginActivity
        sUsername = getIntent().getStringExtra("USERNAME");

        // get sessionId from extras set by LoginActivity
        String sessionId = getIntent().getStringExtra("EXTRA_SESSION_ID");
        sessionId = sessionId.replace("session=", "");
        sessionId = sessionId.replace("; HttpOnly; Path=/", "");
        sId = sessionId;

        sIsAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init action bar
        ActionBar actionBar = getSupportActionBar();
        DrawerLayout drawerLayout;

        // importing linearlayout for buttons
        // nested in a scrollview for scrolling
        LinearLayout restaurantBoard = findViewById(R.id.l1);

        // set the size of the individual buttons
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(1000, 600);
        Integer[] restIcons = new Integer[]{
                R.drawable.bobs_burgers,
                R.drawable.alice_apple,
                R.drawable.peter_pie,
                R.drawable.djronald,
                R.drawable.hasburger,
                R.drawable.segway,
                R.drawable.taco_ball,
                R.drawable.vip_lounge
        };

        OkHttpGetRequest getReq = new OkHttpGetRequest();

        Response response = getReq.doGetRequest("restaurant/all/", sId);
        JSONObject json;

        // Getting restaurant data from backend
        try {

            String responseData = Objects.requireNonNull(response.body()).string();
            json = new JSONObject(responseData);
            JSONArray jsonData = json.getJSONArray("restaurants");
            Log.d("asd jsonData", jsonData.toString());
            for (int i = 0; i < jsonData.length(); i++) {
                JSONObject js = jsonData.getJSONObject(i);
                restNames.add(js.getString("name"));
                restDesc.add(js.getString("description"));
                restItems.add(js.getJSONObject("products"));
            }
        } catch (IOException | JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        Log.d("restaurant items now", restItems.toString());
        Log.d("restaurant names now: ", restNames.toString());


        // Loop for getting restaurant items from request
        for (int i = 0; i < restNames.size(); i++) {
            Button btn = new Button(this);
            btn.setId(i + 1);
            Spannable span = new SpannableString(restNames.get(i) + "\n\"" + restDesc.get(i) + "\"");
            btn.setText(span);
            btn.setContentDescription(restItems.get(i).toString());
            btn.setTextSize(20);
            btn.setLayoutParams(lp);
            btn.setOnClickListener(restaurantButtonListener);
            // set the icon for button
            Drawable icon = ResourcesCompat.getDrawable(getApplicationContext().getResources(),
                    restIcons[i], null);
            assert icon != null;
            Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
            Drawable resizedIcon = new BitmapDrawable(getResources(),
                    Bitmap.createScaledBitmap(bitmap, 160, 160, true));
            btn.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    resizedIcon, null);
            restaurantBoard.addView(btn);
        }

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.nav_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.nav_open,
                R.string.nav_close);
        // pass the Open and Close toggle for the drawer layout listener
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // to make the Navigation drawer icon always appear on the action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setSubtitle(R.string.sub_main);
        }
        // implement navigation view to use nav_drawer buttons for navigation in the app
        navigationView = findViewById(R.id.navigation_view);
        // show admin navigation item
        navigationView.getMenu().findItem(R.id.nav_admin).setVisible(sIsAdmin);
        navigationView.setNavigationItemSelectedListener(this);

        getLicenseFile();
    }

    // click listener for the navigation drawer items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent myIntent;
        // define action for each navigation drawer button
        try {
            if (id == R.id.nav_account) {
                myIntent = new Intent(getApplicationContext(), AccountActivity.class);
                myIntent.putExtra("USERNAME", sUsername);
                startActivity(myIntent);
            } else if (id == R.id.nav_admin) {
                myIntent = new Intent(getApplicationContext(), AdminActivity.class);
                myIntent.putExtra("EXTRA_SESSION_ID", sId);
                startActivity(myIntent);
            } else if (id == R.id.nav_orders) {
                myIntent = new Intent(getApplicationContext(), OrdersActivity.class);
                startActivity(myIntent);
            } else if (id == R.id.nav_about) {
                myIntent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(myIntent);
            } else if (id == R.id.nav_logout) {
                OkHttpPostRequest logoutPostReq = new OkHttpPostRequest();
                Response resp = logoutPostReq.doPostRequest("users/logout/", null, sId);
                if (resp.toString().contains("200")) {
                    resp.close();
                    myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(myIntent);
                    this.finish();
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                }
                resp.close();
            } else if (id == R.id.nav_dark_mode) {
                int mode = AppCompatDelegate.getDefaultNightMode();
                if ((mode == MODE_NIGHT_NO) || (mode == MODE_NIGHT_UNSPECIFIED)) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
                }
            } else {
                throw new Exception("Invalid item clicked!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // click listener for the navigation drawer toggle
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // get license file from backend
    private void getLicenseFile() {
        if (!checkPermission()) {
            requestPermission();
        }
        OkHttpGetRequest getReq = new OkHttpGetRequest();
        Response response = getReq.doGetRequest("file/", sId);

        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/Documents/" + "license.pdf");
            ResponseBody rb = response.body();
            FileOutputStream fOut = new FileOutputStream(file);
            assert rb != null;
            InputStream bodyStream = rb.byteStream();
            byte[] buffer = new byte[1024];
            int n;
            do {
                n = bodyStream.read(buffer, 0, 1024);
                //Log.d("Read bytes:", String.valueOf(n));
                if (n >= 0) fOut.write(buffer, 0, n);
            } while (n != -1);
            fOut.close();
            Log.d("File operation", "File save successful!");
            //Toast.makeText(this, "File get success!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // check if device has given permission to write to external memory
    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    // request permission to write to external memory
    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

}