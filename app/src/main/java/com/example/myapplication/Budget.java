package com.example.myapplication;
import java.util.Map;
public class Budget {
    private int totalBudget;
    private Map<String, Integer> categoryValues;

    private String username;

    private String monthandyear;

    public Budget() {}

    public Budget(String username, int totalBudget, Map<String, Integer> categoryValues,String monthandyear) {
        this.username = username;
        this.totalBudget = totalBudget;
        this.categoryValues = categoryValues;

        this.monthandyear=monthandyear;
    }
    public int getTotalBudget() {
        return totalBudget;
    }

    public Map<String, Integer> getCategoryValues() {
        return categoryValues;
    }



    public String getUsername() {
        return username;
    }

    public String getMonthandyear() {
        return monthandyear;
    }


}
