package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.MyViewHolder> {
    private final OnItemsClick onItemsClick;
    private final List<ExpenseModel> expenseModelList;

    public ExpensesAdapter(Context context, OnItemsClick onItemsClick) {
        expenseModelList=new ArrayList<>();
        this.onItemsClick=onItemsClick;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void add(ExpenseModel expenseModel){
        expenseModelList.add(expenseModel);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear(){
        expenseModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ExpenseModel expenseModel=expenseModelList.get(position);
        holder.note.setText(expenseModel.getNote());
        holder.category.setText(expenseModel.getCategory());
        holder.amount.setText(String.valueOf(expenseModel.getAmount()));

        holder.itemView.setOnClickListener(v -> onItemsClick.onClick(expenseModel));
    }

    @Override
    public int getItemCount() {
        return expenseModelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView note;
        private final TextView category;
        private final TextView amount;
        //public Object note,category,amount,date;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            note=itemView.findViewById(R.id.note);
            category=itemView.findViewById(R.id.category);
            amount=itemView.findViewById(R.id.amount);
            TextView date = itemView.findViewById(R.id.date);
        }
    }
}
