package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Home extends AppCompatActivity implements OnItemsClick{
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    ActivityHomeBinding binding;
    private ExpensesAdapter expensesAdapter;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        Toolbar myToolbar = binding.myToolbar2;
        setContentView(binding.getRoot());
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

        NavigationView navigationView = binding.navigationView2;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    showToast("Home Clicked");
                    Intent home=new Intent(Home.this,Home.class);
                    startActivity(home);
                } else if (itemId == R.id.nav_report) {
                    showToast("Report Clicked");
                    Intent report=new Intent(Home.this, MonthlyReport.class);
                    startActivity(report);
                } else if (itemId == R.id.nav_wallet) {
                    showToast("Budget Clicked");
                    Intent demo2=new Intent(Home.this,budgetdemo.class);
                    startActivity(demo2);

                } else if (itemId == R.id.nav_profile) {
                    showToast("Personal Profile Clicked");
                    Intent profile=new Intent(Home.this,profie.class);
                    startActivity(profile);
                } else if (itemId == R.id.nav_setting) {
                    showToast("Setting Clicked");
                    Intent setting=new Intent(Home.this, Setting.class);
                    startActivity(setting);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        expensesAdapter=new ExpensesAdapter(this,this);
        binding.recycler.setAdapter(expensesAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        intent=new Intent(Home.this,AddExpense.class);

        binding.addExpense.setOnClickListener(v -> {
            intent.putExtra("type","expense");
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCancelable(false);

        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            progressDialog.show();
            FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            progressDialog.cancel();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.cancel();
                            Toast.makeText(Home.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void getData() {
        // Step 1: Get the username from SharedPreferences
        String username = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("userName", "");

        // Step 2: Query expenses based on the username
        FirebaseFirestore
                .getInstance()
                .collection("expense")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Process the query results and update the adapter
                        expensesAdapter.clear();
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : dsList) {
                            ExpenseModel expenseModel = documentSnapshot.toObject(ExpenseModel.class);
                            expensesAdapter.add(expenseModel);
                        }
                    }
                });
    }


    @Override
    public void onClick(ExpenseModel expenseModel) {
        intent.putExtra("model", expenseModel);
        startActivity(intent);
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void updateTotalRemaining() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String username = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("userName", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String yearMonth = sdf.format(new Date());

        // Step 1: Retrieve total expenses from the "Total expenses" collection
        db.collection("Total expenses")
                .document(username + "_" + yearMonth)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        long totalExpenses = documentSnapshot.getLong("totalExpenses");

                        // Step 2: Retrieve total budget from the "budgets" collection
                        db.collection("budgets")
                                .document(username + "_" + yearMonth)
                                .get()
                                .addOnSuccessListener(budgetDocument -> {
                                    if (budgetDocument.exists()) {
                                        long totalBudget = budgetDocument.getLong("totalBudget");

                                        // Step 3: Calculate remaining budget
                                        long remainingBudget = totalBudget - totalExpenses;

                                        // Step 4: Save remaining budget in the "totalsaved" collection
                                        saveRemainingBudget(username, yearMonth, remainingBudget);

                                        // Step 5: Update totalRemainTextView
                                        TextView totalRemainTextView = binding.totalremain;
                                        totalRemainTextView.setText(String.valueOf(remainingBudget));
                                    } else {
                                        showToast("Budget document does not exist");
                                    }
                                })
                                .addOnFailureListener(e -> showToast("Failed to retrieve total budget: " + e.getMessage()));
                    } else {
                        showToast("Total expenses document does not exist");
                    }
                })
                .addOnFailureListener(e -> showToast("Failed to retrieve total expenses: " + e.getMessage()));
    }


    private void saveRemainingBudget(String username, String yearMonth, long remainingBudget) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Step 5: Save remaining budget in the "totalsaved" collection
        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put("remainingBudget", remainingBudget);
        dataToSave.put("name", username);

        db.collection("totalsaved")
                .document(username + "_" + yearMonth)
                .set(dataToSave, SetOptions.merge());

    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
        updateTotalRemaining();
        expensesAdapter.notifyDataSetChanged();

    }




}