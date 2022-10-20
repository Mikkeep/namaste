package com.example.namaste;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // placeholder restaurant name array for buttons (replace with results of a query of backend)
        String[] restNames = new String[]{"Bob's Burgers", "Alice's Apples", "Peter's Pies", "DjRonalds", "Hasburger", "Segway", "Taco Ball"};
        String[] restDesc = new String[]{"Best burgers!", "Delicious Apples", "Mmm...pies", "What is McDonalds?", "Its in the game", "Eat fresh", "TexMex restaurant"};

        // importing linearlayout for buttons
        // nested in a scrollview for scrolling
        LinearLayout restaurantBoard = (LinearLayout) findViewById(R.id.l1);
        // set the size of the individual buttons
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(1000, 300);
        for (int i = 0; i < restNames.length; i++) {
            Button btn = new Button(this);
            btn.setId(i);
            Spannable span = new SpannableString(restNames[i]+"\n-"+restDesc[i]);
            btn.setText(span);
            btn.setLayoutParams(lp);
            btn.setOnClickListener(restaurantButtonListener);
            // sets the icon for button
            Drawable icon = getApplicationContext().getResources().getDrawable(R.drawable.burger);
            Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
            Drawable resizedIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 200, 200, true));
            btn.setCompoundDrawablesWithIntrinsicBounds(null, null, resizedIcon, null);
            restaurantBoard.addView(btn);
        }

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.nav_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // click listener for the restaurant buttons
    private View.OnClickListener restaurantButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // check which button was clicked
            Button btn = (Button) v;
            Toast.makeText(MainActivity.this, "Clicked button " + v.getId(), Toast.LENGTH_SHORT).show();
        }
    };

}
