package com.example.myapplication;

public class ReportModel {
    private String category;
    private double amount;
    private String budget;
    private int image;

    public ReportModel(String category, double amount, int imageResource) {
        this.category = category;
        this.amount = amount;
        this.image = imageResource;
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
        this.amount = amount;}

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public ReportModel() {
    }

    public ReportModel(String category, double amount, String budget, int image) {
        this.category = category;
        this.amount = amount;
        this.budget = budget;
        this.image = image;
    }
}
