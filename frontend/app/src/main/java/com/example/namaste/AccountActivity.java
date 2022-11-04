package com.example.namaste;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setSubtitle(R.string.sub_account);
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView userText = findViewById(R.id.account_profile_username);

        String username = this.getIntent().getStringExtra("USERNAME");
        String usernameSuffixed;
        if (username.endsWith("s")) {
             usernameSuffixed = username.concat("' ");
        } else {
            usernameSuffixed = username.concat("'s ");
        }
        String usernameText = usernameSuffixed + getText(R.string.account_user_suffix);
        userText.setText(usernameText);

    }
}