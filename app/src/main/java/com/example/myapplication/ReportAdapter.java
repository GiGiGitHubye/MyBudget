package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    Context context;
    ArrayList<ReportModel> list;

    public ReportAdapter(Context context, ArrayList<ReportModel> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.report_row,parent,false);
        return new ReportViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportModel reportModel=list.get(position);
        holder.categoryimg.setImageResource(reportModel.getImage());
        holder.category.setText(reportModel.getCategory());
        if (holder.amount != null) {
            holder.amount.setText(String.format(Locale.getDefault(), "%.2f", reportModel.getAmount()));
        }
        //holder.amount.setText(String.format(Locale.getDefault(), "%.2f", reportModel.getAmount()));
        //holder.budget.setText(reportModel.getBudget());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder{

        TextView category,amount,budget;
        ImageView categoryimg;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryimg = itemView.findViewById(R.id.iconImageView);
            category = itemView.findViewById(R.id.report_category);
            amount = itemView.findViewById(R.id.report_amount);
            //budget=itemView.findViewById(R.id.?);
        }
    }

}
