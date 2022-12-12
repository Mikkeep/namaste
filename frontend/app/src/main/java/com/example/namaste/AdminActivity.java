package com.example.namaste;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Response;

public class AdminActivity extends AppCompatActivity {

    private ArrayList<String> uNames = new ArrayList<>();
    private ArrayList<String> pWords = new ArrayList<>();
    private ArrayList<String> userStatuses = new ArrayList<>();
    private String sId;
    private ListView lLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        sId = getIntent().getStringExtra("EXTRA_SESSION_ID");
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setSubtitle(R.string.sub_admin);
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView adminPass = findViewById(R.id.re_enter_admin_field);
        Button adminBtn = findViewById(R.id.adminBtn);
        adminBtn.setOnClickListener(view -> {
            if (adminPass.getText().toString().equals("supersecurepassword123456")) {
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.activity_admin_panel);
                lLayout = findViewById(R.id.listView);

                updateUsers();
                lLayout.setOnItemClickListener((adapterView, view1, pos, id) -> {
                    TextView tV = (TextView) view1.findViewById(R.id.adminListItem_title);
                    TextView subTV = (TextView) view1.findViewById(R.id.adminListItem_sub);
                    String userName = tV.getText().toString();
                    String subTitle = subTV.getText().toString().split("\\|")[0].trim();
                    boolean userAdmin = subTitle.equals("Admin");
                    String userActionMsg = userAdmin ?
                            "Demote user: "+userName+" from admin?"
                            : "Promote user: "+userName+" to admin?";

                    AlertDialog dialog = buildAdminDialog(userActionMsg, userName, !userAdmin);
                    dialog.show();
                });

            } else {
                Toast.makeText(this, "Invalid admin password", Toast.LENGTH_SHORT).show();
            }
        }
    );

    }

    private AlertDialog buildAdminDialog(String msg, String userName , boolean elevate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
            // OK Clicked
            OkHttpPostRequest adminReq = new OkHttpPostRequest();
            String elevateCommand = elevate ? "elevate" : "delevate";
            String message = null;
            try {
                message = new JSONObject().put("command", userName).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adminReq.doPostRequest(
                    "users/admin/"+elevateCommand+"/",
                    message,
                    sId
                    );
            updateUsers();
        });
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
            // Action canceled: Nothing happens
        });
        return builder.create();
    }

    private void updateUsers() {

        OkHttpGetRequest getReq = new OkHttpGetRequest();
        Response response = getReq.doGetRequest("users", sId);
        JSONObject json;

        JSONArray jsonData = new JSONArray();
        // Getting user data from backend
        try {
            String responseData = Objects.requireNonNull(response.body()).string();
            json = new JSONObject(responseData);
            for (int i=0;i<json.length();i++) {
                jsonData.put(json.getJSONObject(String.valueOf(i)));
            }
        } catch (IOException | NullPointerException | JSONException e) {
            e.printStackTrace();
        }
        response.close();

        try {
            uNames.clear();
            pWords.clear();
            userStatuses.clear();
            for (int i=0;i<jsonData.length();i++) {
                JSONObject js = jsonData.getJSONObject(i);
                String uName = js.getString("username");
                String pWord = js.getString("password");

                // for some reason backend mixes up booleans and integers in admin values so we check for both
                boolean isAdmin;
                try {
                    isAdmin = (js.getInt("is_admin") == 1);
                } catch (JSONException e) {
                    isAdmin = js.getBoolean("is_admin");
                }

                String adminStatus = isAdmin ? "Admin" : "User";

                uNames.add(uName);
                pWords.add(pWord);
                userStatuses.add(adminStatus);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        lLayout.setAdapter(new ListAdapter());
    }


    private class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return uNames.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View returnView = getLayoutInflater().inflate(R.layout.admin_listview_item, null);

            TextView uName = returnView.findViewById(R.id.adminListItem_title);
            TextView desc = returnView.findViewById(R.id.adminListItem_sub);

            String descText = userStatuses.get(i) + " | Password: " + pWords.get(i);

            uName.setText(uNames.get(i));
            desc.setText(descText);

            return returnView;
        }

    }
}