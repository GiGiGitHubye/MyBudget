package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class budgetdemo extends AppCompatActivity {
    private final List<String> categoryNamesList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    Budget budget;
    private final int[] iconResources = {
            R.drawable.budget_bill_icon,
            R.drawable.budget_collision_icon,
            R.drawable.budget_dish_icon,
            R.drawable.budget_electricity_icon,
            R.drawable.buget_water_icon,
            R.drawable.budget_entertainment_icon,
            R.drawable.budget_grocery_icon,
            R.drawable.budget_injure_icon,
            R.drawable.budget_learning_icon,
            R.drawable.budget_pet_icon,
            R.drawable.budget_sport_icon,
            R.drawable.budget_transport_icon,
            R.drawable.budget_girlfriend_icon,
            R.drawable.budget_other_icon
    };

    private int selectedIconPosition = 0;
    private LinearLayout categories_list;
    private final List<EditText> iconAmountList = new ArrayList<>();
    private final Set<String> categoryNamesSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        budget=new Budget();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgetdemo);
        Toolbar myToolbar = findViewById(R.id.my_toolbar3);
        setSupportActionBar(myToolbar);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.nav_open, R.string.nav_close);
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
                Intent home=new Intent(budgetdemo.this,Home.class);
                startActivity(home);
            } else if (itemId == R.id.nav_report) {
                showToast("Report Clicked");
                Intent report=new Intent(budgetdemo.this, MonthlyReport.class);
                startActivity(report);
            } else if (itemId == R.id.nav_wallet) {
                showToast("Budget Clicked");
                Intent demo2=new Intent(budgetdemo.this,budgetdemo.class);
                startActivity(demo2);

            } else if (itemId == R.id.nav_profile) {
                showToast("Personal Profile Clicked");
                Intent profile=new Intent(budgetdemo.this,profie.class);
                startActivity(profile);
            } else if (itemId == R.id.nav_setting) {
                showToast("Setting Clicked");
                Intent setting=new Intent(budgetdemo.this, Setting.class);
                startActivity(setting);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        categories_list = findViewById(R.id.categories_list);
        Button add_Button = findViewById(R.id.budget_add_icon);
        add_Button.setOnClickListener(v -> showAddPageDialog());

        Button budget_confirm_icon = findViewById(R.id.budget_confirm_icon);
        budget_confirm_icon.setOnClickListener(v -> {

            int enteredAmount = getEnteredAmount();
            int usedAmount = calculateUsedAmount();

            if (usedAmount > enteredAmount) {
                showToast("Warning: You have exceeded the total budget");
            } else if(usedAmount<enteredAmount){
                showToast("Warning: You amount is wrong");
                }
            else {
                saveBudgetToFirestore();

                finish();
            }
        });
    }

    private void showAddPageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_dialog, null);

        Spinner iconSpinner = dialogView.findViewById(R.id.iconSpinner);
        ImageView iconImageView = dialogView.findViewById(R.id.iconImageView);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.icon_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        iconSpinner.setAdapter(adapter);

        iconSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedIconPosition = position;


                iconImageView.setImageResource(iconResources[selectedIconPosition]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String categoryName = selectedIconPosition == iconResources.length - 1 ? "Other" : getResources().getStringArray(R.array.icon_array)[selectedIconPosition];

            if (isCategoryNameUnique(categoryName)) {
                addView(selectedIconPosition, categoryName);

            } else {
                showToast("Category already exists");
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isCategoryNameUnique(String categoryName) {
        return categoryNamesSet.add(categoryName);
    }

    private void addView(int selectedIconPosition, String categoryName) {
        @SuppressLint("InflateParams") View adding = getLayoutInflater().inflate(R.layout.add_categories, null, false);

        ImageView icon_showIV = adding.findViewById(R.id.icon_showIV);
        ImageButton budget_delete_icon = adding.findViewById(R.id.budget_delete_icon);
        EditText icon_amountET = adding.findViewById(R.id.icon_amountET);

        if (selectedIconPosition == iconResources.length - 1) {
            icon_showIV.setTag(categoryName);
            icon_showIV.setOnClickListener(v -> showToast(categoryName));
        } else {
            icon_showIV.setTag(categoryName);
            icon_showIV.setOnClickListener(v -> showToast(categoryName));
        }

        icon_showIV.setImageResource(iconResources[selectedIconPosition]);

        budget_delete_icon.setOnClickListener(v -> removeView(adding, categoryName));

        adding.setPadding(0, 18, 0, 0);
        categories_list.addView(adding);

        // Add the category name to the list
        categoryNamesList.add(categoryName);

        // Add the newly created EditText to the iconAmountList
        iconAmountList.add(icon_amountET);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void removeView(View view, String categoryName) {
        categories_list.removeView(view);
        iconAmountList.remove(((ViewGroup) view).findViewById(R.id.icon_amountET));
        categoryNamesSet.remove(categoryName);
    }


    private int getEnteredAmount() {
        EditText totalbudget = findViewById(R.id.budget_totalET);
        String enteredAmountStr = totalbudget.getText().toString();
        return enteredAmountStr.isEmpty() ? 0 : Integer.parseInt(enteredAmountStr);
    }

    private int calculateUsedAmount() {
        int usedAmount = 0;
        // Iterate through the categories_list and sum up the amounts
        for (int i = 0; i < categories_list.getChildCount(); i++) {
            View categoryView = categories_list.getChildAt(i);
            // Extract the amount from the category view
            EditText amountET = categoryView.findViewById(R.id.icon_amountET);
            String amountStr = amountET.getText().toString();
            int amount = amountStr.isEmpty() ? 0 : Integer.parseInt(amountStr);
            usedAmount += amount;
        }
        return usedAmount;
    }

    private void saveBudgetToFirestore() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username =prefs.getString("userName","");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String yearMonth = sdf.format(new Date());
        String documentId = username + "_" + yearMonth;

        int totalBudget = getEnteredAmount();
        Map<String, Integer> categoryValues = getCategoryValues();
        int remainingBudget = totalBudget - calculateUsedAmount();

        Budget budget = new Budget(username,totalBudget, categoryValues, yearMonth);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("budgets")
                .document(documentId)
                .set(budget)
                .addOnSuccessListener(aVoid -> showToast("Budget saved successfully"))
                .addOnFailureListener(e -> showToast("Failed to save budget: " + e.getMessage()));
    }

    private Map<String, Integer> getCategoryValues() {
        Map<String, Integer> categoryValues = new HashMap<>();
        for (int i = 0; i < categories_list.getChildCount(); i++) {
            View categoryView = categories_list.getChildAt(i);
            String categoryName = categoryNamesList.get(i); // Get the category name from the list
            EditText amountET = categoryView.findViewById(R.id.icon_amountET);
            int amount = amountET.getText().toString().isEmpty() ? 0 : Integer.parseInt(amountET.getText().toString());
            categoryValues.put(categoryName, amount);
        }
        return categoryValues;
    }




}










