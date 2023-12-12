package com.example.myapplication;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityAddExpenseBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddExpense extends AppCompatActivity {
    ActivityAddExpenseBinding binding;
    private String type;
    private ExpenseModel expenseModel;

    private List<String> categoryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryList = new ArrayList<>();
        categoryList.add("Student Bill");
        categoryList.add("Collision");
        categoryList.add("Food");
        categoryList.add("Electricity");
        categoryList.add("Water");
        categoryList.add("Entertainment");
        categoryList.add("Grocery");
        categoryList.add("Injure");
        categoryList.add("Learning");
        categoryList.add("Pet");
        categoryList.add("Sport");
        categoryList.add("Transportation");
        categoryList.add("Girlfriend");
        categoryList.add("Other");
        type=getIntent().getStringExtra("type");
        expenseModel=(ExpenseModel) getIntent().getSerializableExtra("model");

        binding.category.setOnClickListener(v -> showCategoryListDialog());

        if (type==null){
            //type=expenseModel.getType();
            binding.amount.setText(String.valueOf(expenseModel.getAmount()));
            binding.category.setText(expenseModel.getCategory());
            binding.note.setText(expenseModel.getNote());
        }


        TextView textView = findViewById(R.id.saveExpense);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = textView.getId();

                if (id==R.id.saveExpense){
                    if (type!=null){
                        createExpense();
                    }else {
                        updateExpense();
                    }
                }
            }
        });

        TextView textView1 = findViewById(R.id.deleteExpense);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id=textView1.getId();
                if (id==R.id.deleteExpense){
                    deleteExpense();
                }
            }
        });

    }

    private void showCategoryListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category");

        String[] categories = categoryList.toArray(new String[0]);

        builder.setItems(categories, (dialog, which) -> {
            String selectedCategory = categoryList.get(which);
            binding.category.setText(selectedCategory);
            dialog.dismiss();
        });

        builder.show();
    }

    private void deleteExpense() {
        if (expenseModel == null || expenseModel.getExpenseID() == null) {
            showToast("Expense data is missing");
            return;
        }
        FirebaseFirestore
                .getInstance()
                .collection("expense")
                .document(expenseModel.getExpenseID())
                .delete()
                .addOnSuccessListener(aVoid->{
                    updateTotalExpensesAndUI();
                    updateRemainingBudget();
                    finish();
                });

    }

    private void updateRemainingBudget() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String username = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("userName", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String yearMonth = sdf.format(new Date());

        // Retrieve budget document from Firestore
        String budgetDocumentId = username + "_" + yearMonth;
        db.collection("budgets")
                .document(budgetDocumentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Budget budget = documentSnapshot.toObject(Budget.class);

                        if (budget != null) {
                            int totalBudget = budget.getTotalBudget();
                            Map<String, Integer> categoryValues = budget.getCategoryValues();

                            // Continue with the remaining budget logic here

                        }
                    }
                })
                .addOnFailureListener(e -> showToast("Failed to retrieve budget: " + e.getMessage()));
    }





    private void createExpense() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = prefs.getString("userName", "");
        String expenseID = UUID.randomUUID().toString();
        String note = binding.note.getText().toString();
        String categoryName = binding.category.getText().toString();  // Field Name
        double amount = Double.parseDouble(binding.amount.getText().toString());  // Field Value
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String yearMonth = sdf.format(new Date());

        if (amount <= 0) {
            binding.amount.setError("Invalid amount");
            return;
        }
        if (categoryName.isEmpty()) {
            showToast("Please select a category");
            return;
        }

        // Create a map to represent the expense data
        Map<String, Object> expenseData = new HashMap<>();
        expenseData.put("username", username);
        expenseData.put("expenseID", expenseID);
        expenseData.put("note", note);
        expenseData.put("category", categoryName);
        expenseData.put("amount", amount);
        expenseData.put("time", yearMonth);
        expenseData.put("uid", FirebaseAuth.getInstance().getUid());

        // Add the expense data to the "expense" collection
        FirebaseFirestore.getInstance()
                .collection("expense")
                .document(expenseID)
                .set(expenseData)
                .addOnSuccessListener(aVoid -> {
                    updateTotalExpensesAndUI();
                    updateRemainingBudget();
                    finish();
                });
    }

    private void updateExpense() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = prefs.getString("userName", "");
        String expenseID = expenseModel.getExpenseID();
        String note = binding.note.getText().toString();
        String categoryName = binding.category.getText().toString();  // Field Name
        double amount = Double.parseDouble(binding.amount.getText().toString());  // Field Value

        if (amount <= 0) {
            binding.amount.setError("Invalid amount");
            return;
        }
        if (categoryName.isEmpty()) {
            showToast("Please select a category");
            return;
        }

        // Create a map to represent the updated expense data
        Map<String, Object> updatedExpenseData = new HashMap<>();
        updatedExpenseData.put("username", username);
        updatedExpenseData.put("expenseID", expenseID);
        updatedExpenseData.put("note", note);
        updatedExpenseData.put("category", categoryName);
        updatedExpenseData.put("amount", amount);
        updatedExpenseData.put("time", expenseModel.getTime());
        updatedExpenseData.put("uid", FirebaseAuth.getInstance().getUid());

        // Update the expense data in the "expense" collection
        FirebaseFirestore.getInstance()
                .collection("expense")
                .document(expenseID)
                .set(updatedExpenseData)
                .addOnSuccessListener(aVoid -> {
                    updateTotalExpensesAndUI();
                    updateRemainingBudget();
                    finish();
                });
    }


    private void updateTotalExpensesAndUI() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String username = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("userName", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String yearMonth = sdf.format(new Date());

        // Initialize a map to store category-wise expenses
        Map<String, Double> categoryExpenses = new HashMap<>();

        // Get the total expenses for the current month
        db.collection("expense")
                .whereEqualTo("username", username)
                .whereEqualTo("time", yearMonth)  // Updated to use "time" field instead of "yearMonth"
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Process each expense document
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        ExpenseModel expenseModel = documentSnapshot.toObject(ExpenseModel.class);

                        // Sum up expenses for each category
                        String category = expenseModel.getCategory();
                        double expenseAmount = expenseModel.getAmount();

                        categoryExpenses.put(category, categoryExpenses.getOrDefault(category, 0D) + expenseAmount);
                    }

                    // Save the total expenses to the "remain" collection
                    saveTotalExpensesToFirestore(categoryExpenses);
                })
                .addOnFailureListener(e -> {
                    // Handle failure to retrieve expenses from Firestore
                    showToast("Failed to retrieve expenses: " + e.getMessage());
                });
    }

    private void saveTotalExpensesToFirestore(Map<String, Double> categoryExpenses) {
        // Calculate total expenses by summing up all category expenses
        double totalExpenses = categoryExpenses.values().stream().mapToDouble(Double::doubleValue).sum();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String username = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("userName", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String yearMonth = sdf.format(new Date());

        // Create a document ID for the "remain" collection
        String documentId = username + "_" + yearMonth;

        // Create a map to represent the data to be saved
        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put("totalExpenses", totalExpenses);
        dataToSave.put("username",username);
        dataToSave.putAll(categoryExpenses);  // Include category-wise expenses in the data

        // Save the total and category-wise expenses to the "remain" collection
        db.collection("Total expenses")
                .document(documentId)
                .set(dataToSave)
                .addOnSuccessListener(aVoid -> showToast("Total and category-wise expenses saved  successfully"))
                .addOnFailureListener(e -> showToast("Failed to save total and category-wise expenses  " + e.getMessage()));
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}