package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class profie extends AppCompatActivity {

    Button logoutbtn;
    TextView currentname;
    TextView changepwd;
    database db;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db=new database();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profie);

        Toolbar myToolbar = findViewById(R.id.my_toolbar3);
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

        NavigationView navigationView = findViewById(R.id.navigation_view3);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                showToast("Home Clicked");
                Intent home=new Intent(profie.this,Home.class);
                startActivity(home);
            } else if (itemId == R.id.nav_report) {
                showToast("Report Clicked");
                Intent report=new Intent(profie.this, MonthlyReport.class);
                startActivity(report);
            } else if (itemId == R.id.nav_wallet) {
                showToast("Budget Clicked");
                Intent demo2=new Intent(profie.this,budgetdemo.class);
                startActivity(demo2);

            } else if (itemId == R.id.nav_profile) {
                showToast("Personal Profile Clicked");
                Intent profile=new Intent(profie.this,profie.class);
                startActivity(profile);
            } else if (itemId == R.id.nav_setting) {
                showToast("Setting Clicked");
                Intent setting=new Intent(profie.this, Setting.class);
                startActivity(setting);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        currentname = findViewById(R.id.currentname);
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String storedUserName = preferences.getString("userName", "");
        currentname.setText(storedUserName);
        changepwd = findViewById(R.id.chgpwd);
        changepwd.setPaintFlags(changepwd.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        changepwd.setClickable(true);
        logoutbtn = findViewById(R.id.logout);
        logoutbtn.setOnClickListener(v -> {
            db.removesharepreference(profie.this);
            Intent intent = new Intent(profie.this, Login.class);
            startActivity(intent);
        });
        changepwd.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(profie.this);
            builder.setTitle("Change Password");

            // Set up the layout
            LinearLayout layout = new LinearLayout(profie.this);
            layout.setOrientation(LinearLayout.VERTICAL);

            // Set up the input fields
            final EditText oldPasswordInput = new EditText(profie.this);
            oldPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            oldPasswordInput.setHint("Enter old password");

            final EditText newPasswordInput = new EditText(profie.this);
            newPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            newPasswordInput.setHint("Enter new password");

            final EditText confirmPasswordInput = new EditText(profie.this);
            confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmPasswordInput.setHint("Confirm new password");

            // Add the EditText fields to the layout
            layout.addView(oldPasswordInput);
            layout.addView(newPasswordInput);
            layout.addView(confirmPasswordInput);

            // Set the layout for the AlertDialog
            builder.setView(layout);

            builder.setPositiveButton("Change", (dialog, which) -> {
                String oldPassword = oldPasswordInput.getText().toString();
                String newPassword = newPasswordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();

                if (confirmPassword.equals(newPassword)) {
                    if (newPassword.length() >= 6) {
                        db.changepassword(storedUserName, oldPassword, newPassword, profie.this);
                    } else {
                        Toast.makeText(profie.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(profie.this, "New password has been typed incorrectly", Toast.LENGTH_SHORT).show();
                }
            });

            // Show the AlertDialog
            builder.show();
        });

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}