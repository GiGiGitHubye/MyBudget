package com.example.myapplication;

import java.io.Serializable;

public class ExpenseModel implements Serializable {
    private String expenseID;
    private String note;
    private String uid;
    private String username;
    private String time;
    private String category; // Updated to store a single category name
    private double amount;

    public ExpenseModel() {
        // Default constructor required for Firestore
    }

    public ExpenseModel(String expenseID, String note, String uid, String username, String time, String category, double amount) {
        this.expenseID = expenseID;
        this.note = note;
        this.uid = uid;
        this.username = username;
        this.time = time;
        this.category = category;
        this.amount = amount;
    }

    public ExpenseModel(double amount, String time, String note, String category) {
        this.note = note;
        this.category=category;
        this.time = time;
        this.amount = amount;
    }

    public String getExpenseID() {
        return expenseID;
    }

    public void setExpenseID(String expenseID) {
        this.expenseID = expenseID;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
