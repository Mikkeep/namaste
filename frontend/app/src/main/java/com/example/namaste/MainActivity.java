package com.example.namaste;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init navigation drawer and action bar
        DrawerLayout drawerLayout;
        ActionBar actionBar = getSupportActionBar();


        // importing linearlayout for buttons
        // nested in a scrollview for scrolling
        LinearLayout restaurantBoard = (LinearLayout) findViewById(R.id.l1);
        // set the size of the individual buttons
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(1000, 300);


        // placeholder restaurant name array for buttons (replace with results of backend query)
        String[] restNames = new String[]
                {"Bob's Burgers", "Alice's Apples", "Peter's Pies",
                "DjRonald's", "HazBurger", "Segway", "Taco Ball"};
        String[] restDesc = new String[]
                {"Best burgers by big boss Bob!", "My apples bring all boys to the yard", "Mmm...pies",
                "Who's McDonald?", "Can I haz cheezburger?", "Eat fast", "You can't resist our balls"};
        Integer[] restIcons = new Integer[]
                       {R.drawable.bobs_burgers, R.drawable.alice_apple, R.drawable.peter_pie,
                        R.drawable.djronald, R.drawable.hasburger, R.drawable.segway,
                        R.drawable.taco_ball};

        // Add restaurant buttons in loop
        for (int i = 0; i < restNames.length; i++) {
            Button btn = new Button(this);
            btn.setId(i);
            Spannable span = new SpannableString(restNames[i]+"\n\""+restDesc[i]+"\"");
            btn.setText(span);
            btn.setLayoutParams(lp);
            btn.setOnClickListener(restaurantButtonListener);

            // set the icon for button
            Drawable icon = ResourcesCompat.getDrawable(getApplicationContext().getResources(),
                    restIcons[i], null);
            assert icon != null;
            Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
            Drawable resizedIcon = new BitmapDrawable(getResources(),
                    Bitmap.createScaledBitmap(bitmap, 90, 90, true));
            btn.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    resizedIcon, null);
            restaurantBoard.addView(btn);
        }

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.nav_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setSubtitle(R.string.sub_main);
        }

        // implement navigation view to use nav_drawer buttons for navigation in the app
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
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
            } else if (id == R.id.nav_cart) {
                myIntent = new Intent(getApplicationContext(), CartActivity.class);
            } else if (id == R.id.nav_orders) {
                myIntent = new Intent(getApplicationContext(), OrdersActivity.class);
            } else if (id == R.id.nav_about) {
                myIntent = new Intent(getApplicationContext(), AboutActivity.class);
            } else if (id == R.id.nav_logout) {
                myIntent = new Intent(getApplicationContext(), LoginActivity.class);
            } else {
                throw new Exception("Invalid item clicked!");
            }
            startActivity(myIntent);
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

    // click listener for the restaurant buttons
    private final View.OnClickListener restaurantButtonListener = v -> {
        // check which button was clicked
        Button btn = (Button) v;
        Toast.makeText(MainActivity.this, "Clicked button " + btn.getId(), Toast.LENGTH_SHORT).show();
    };


}
