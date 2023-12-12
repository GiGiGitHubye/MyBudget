package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MonthlyReport extends AppCompatActivity {
    PieChart pieChart;
    List<PieEntry> pieEntryList = new ArrayList<>();
    private Map<String, Double> categorySums = new HashMap<>();
    RecyclerView recyclerView;
    ArrayList<ReportModel>reportModels;
    DatabaseReference databaseReference;
    ReportAdapter reportAdapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report);

        Toolbar myToolbar = findViewById(R.id.my_toolbar4);
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

        NavigationView navigationView = findViewById(R.id.navigation_view4);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                showToast("Home Clicked");
                Intent home=new Intent(MonthlyReport.this,Home.class);
                startActivity(home);
            } else if (itemId == R.id.nav_report) {
                showToast("Report Clicked");
                Intent report=new Intent(MonthlyReport.this, MonthlyReport.class);
                startActivity(report);
            } else if (itemId == R.id.nav_wallet) {
                showToast("Budget Clicked");
                Intent demo2=new Intent(MonthlyReport.this,budgetdemo.class);
                startActivity(demo2);

            } else if (itemId == R.id.nav_profile) {
                showToast("Personal Profile Clicked");
                Intent profile=new Intent(MonthlyReport.this,profie.class);
                startActivity(profile);
            } else if (itemId == R.id.nav_setting) {
                showToast("Setting Clicked");
                Intent setting=new Intent(MonthlyReport.this, Setting.class);
                startActivity(setting);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        pieChart=findViewById(R.id.pieChart);

        getData();

        recyclerView=findViewById(R.id.CategoryExpense);
        databaseReference= FirebaseDatabase.getInstance().getReference("expense");
        reportModels=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(this,reportModels);
        recyclerView.setAdapter(reportAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ReportModel reportModel=dataSnapshot.getValue(ReportModel.class);
                    reportModels.add(reportModel);
                }
                reportAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getData(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String currentMonth = dateFormat.format(calendar.getTime());

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String storedUserName = preferences.getString("userName", "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference expensesCollection = db.collection("expense");

        expensesCollection
                .whereEqualTo("username",storedUserName )
                .whereEqualTo("time",currentMonth)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    double totalExpense = 0;
                    categorySums.clear();

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        ExpenseModel expense = documentSnapshot.toObject(ExpenseModel.class);

                        String category = expense.getCategory();
                        double amount = expense.getAmount();

                        totalExpense += amount;

                        // Sum up the amounts for each category
                        if (categorySums.containsKey(category)) {
                            double currentSum = categorySums.get(category);
                            categorySums.put(category, currentSum + amount);
                        } else {
                            categorySums.put(category, amount);
                        }
                    }

                    for (Map.Entry<String, Double> entry : categorySums.entrySet()) {
                        String category = entry.getKey();
                        double totalAmount = entry.getValue();
                        pieEntryList.add(new PieEntry((float) totalAmount, category));
                    }
                    setUpChart();
                    updateRecyclerView();
                });}

    private void updateRecyclerView() {
        // Clear existing data in reportModels
        reportModels.clear();

        // Create ReportModel objects from categorySums and add to reportModels
        for (Map.Entry<String, Double> entry : categorySums.entrySet()) {
            String category = entry.getKey();
            double amount = entry.getValue();
            int imageResource = getCategoryImage(category); // Implement this method to get the image based on category

            ReportModel reportModel = new ReportModel(category, amount, imageResource);
            reportModels.add(reportModel);
        }

        // Update RecyclerView
        reportAdapter.notifyDataSetChanged();
    }

    private int getCategoryImage(String category) {
        // Create a map to associate categories with image resource IDs
        Map<String, Integer> categoryImageMap = new HashMap<>();
        categoryImageMap.put("Food", R.drawable.budget_dish_icon);
        categoryImageMap.put("Entertainment", R.drawable.budget_entertainment_icon);
        categoryImageMap.put("Student Bill", R.drawable.budget_bill_icon);
        categoryImageMap.put("Collision", R.drawable.budget_collision_icon);
        categoryImageMap.put("Electricity", R.drawable.budget_electricity_icon);
        categoryImageMap.put("Water", R.drawable.buget_water_icon);
        categoryImageMap.put("Injure", R.drawable.budget_injure_icon);
        categoryImageMap.put("Learning", R.drawable.budget_learning_icon);
        categoryImageMap.put("Pet", R.drawable.budget_pet_icon);
        categoryImageMap.put("Sport", R.drawable.budget_sport_icon);
        categoryImageMap.put("Transportation", R.drawable.budget_transport_icon);
        categoryImageMap.put("Girlfriend", R.drawable.budget_girlfriend_icon);


        // Check if the category is in the map
        if (categoryImageMap.containsKey(category)) {
            // Return the corresponding image resource ID
            return categoryImageMap.get(category);
        } else {
            // If the category is not in the map, return a default image
            return R.drawable.budget_other_icon;
        }
    }



    @SuppressLint("DefaultLocale")
    private void setUpChart() {
        Description description = new Description();
        description.setText("Expenses Analysis Chart");

        double totalExpense = calculateTotalExpense();

        PieDataSet pieDataSet=new PieDataSet(pieEntryList,"Expense Categories");
        PieData pieData=new PieData(pieDataSet);
        //pieDataSet.setSliceSpace(2);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.white));
        pieDataSet.setValueTextSize(12);

        if (!pieEntryList.isEmpty()) {
            Log.d("PieChart", "No data available for PieChart");}

        pieChart.clear();
        pieChart.setData(pieData);
        pieChart.setCenterText(String.format("\nTotal Expense\nRM %.2f", totalExpense));
        pieChart.setCenterTextSize(18);
        pieChart.setCenterTextColor(ContextCompat.getColor(this, R.color.black));
        pieChart.setDescription(description);
        pieChart.animateXY(500,500);
        pieChart.invalidate();
    }

    private double calculateTotalExpense() {
        double totalExpense = 0;

        // Calculate total expense from categorySums
        for (Map.Entry<String, Double> entry : categorySums.entrySet()) {
            totalExpense += entry.getValue();
        }

        return totalExpense;
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}