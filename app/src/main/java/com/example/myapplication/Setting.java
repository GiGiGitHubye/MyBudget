package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Setting extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    database database2;
    private static final String DARK_MODE_PREF_KEY = "dark_mode_pref";
    LottieAnimationView settingAnimation;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch darkmodeSettingSwitch;
    Button readSettingsButton, commentSettingButton, resetSettingButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar myToolbar = findViewById(R.id.my_toolbar5);
        setSupportActionBar(myToolbar);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        myToolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigation_view5);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent home=new Intent(Setting.this,Home.class);
                    startActivity(home);
                } else if (itemId == R.id.nav_report) {
                    Intent report=new Intent(Setting.this, MonthlyReport.class);
                    startActivity(report);
                } else if (itemId == R.id.nav_wallet) {
                    Intent demo2=new Intent(Setting.this,budgetdemo.class);
                    startActivity(demo2);
                } else if (itemId == R.id.nav_profile) {
                    Intent profile=new Intent(Setting.this,profie.class);
                    startActivity(profile);
                } else if (itemId == R.id.nav_setting) {
                    Intent setting=new Intent(Setting.this, Setting.class);
                    startActivity(setting);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        database2=new database();
        settingAnimation = findViewById(R.id.lottie_setting_animation);
        settingAnimation.playAnimation();

        darkmodeSettingSwitch = findViewById(R.id.setting_darkmodeSW);
        resetSettingButton = findViewById(R.id.setting_resetBT);
        readSettingsButton = findViewById(R.id.setting_readBT);
        commentSettingButton = findViewById(R.id.setting_commentBT);

        // Get SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Set the initial state based on the saved preference
        boolean isDarkModeEnabled = sharedPreferences.getBoolean(DARK_MODE_PREF_KEY, false);
        darkmodeSettingSwitch.setChecked(isDarkModeEnabled);

        resetSettingButton.setOnClickListener(v -> showAlertDialog());
        readSettingsButton.setOnClickListener(v -> showAboutUsDialog());
        commentSettingButton.setOnClickListener(v -> showCommentDialog());

        darkmodeSettingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // Save the dark mode preference
            sharedPreferences.edit().putBoolean(DARK_MODE_PREF_KEY, isChecked).apply();
        });
    }


    //-------AlertDialog------------
    private void showAlertDialog() {
        View commentView = LayoutInflater.from(this).inflate(R.layout.setting_default, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(commentView)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String userNameToDelete = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("userName", "");
                    deleteDataForName(userNameToDelete);
                    finishAffinity();
                    dialog.dismiss();
                });


        androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
            positiveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));

        });

        alertDialog.show();
    }
    private void deleteDataForName(String name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Delete from "expense" collection
        db.collection("expense")
                .whereEqualTo("username", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        db.collection("expense").document(documentSnapshot.getId()).delete();
                    }
                });
        db.collection("budgets")
                .whereEqualTo("username", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        db.collection("budgets").document(documentSnapshot.getId()).delete();
                    }
                });
        db.collection("Total expenses")
                .whereEqualTo("username", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        db.collection("Total expenses").document(documentSnapshot.getId()).delete();
                    }
                });
        db.collection("users")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        db.collection("users").document(documentSnapshot.getId()).delete();
                    }
                });
        db.collection("facebook")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        db.collection("facebook").document(documentSnapshot.getId()).delete();
                    }
                });
        db.collection("google")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        db.collection("google").document(documentSnapshot.getId()).delete();
                    }
                });
        db.collection("totalsaved")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        db.collection("totalsaved").document(documentSnapshot.getId()).delete();
                    }
                });
        db.collection("feedback")
                .whereEqualTo("username", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        db.collection("feedback").document(documentSnapshot.getId()).delete();
                    }
                });
        database2.removesharepreference(Setting.this);
        Intent intent=new Intent(Setting.this,Login.class);
        startActivity(intent);


    }


    //--------AboutUsDialog-------------
    private void showAboutUsDialog() {
        View aboutUsView = LayoutInflater.from(this).inflate(R.layout.setting_about, null);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(aboutUsView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
            positiveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
        });

        alertDialog.show();
    }

    //-------CommentDialog------------
    private void showCommentDialog() {
        View commentView = LayoutInflater.from(this).inflate(R.layout.setting_feedback, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(commentView);

        // Find the EditText within the dialog view
        EditText commentEditText = commentView.findViewById(R.id.editTextFeedback);

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Submit", (dialog, which) -> {
                    String comment = commentEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(comment)) {
                        // Get the username from SharedPreferences
                        String username = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("userName", "");

                        // Save the comment and username to the "feedback" collection
                        saveFeedback(username, comment);

                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
            positiveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
        });

        alertDialog.show();
    }


    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}