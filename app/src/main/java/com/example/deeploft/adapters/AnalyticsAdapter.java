package com.example.deeploft.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.R;
import com.example.deeploft.models.SalesAnalytics;
import java.util.List;

public class AnalyticsAdapter extends RecyclerView.Adapter<AnalyticsAdapter.AnalyticsViewHolder> {

    private final List<SalesAnalytics> analytics;

    public AnalyticsAdapter(List<SalesAnalytics> analytics) {
        this.analytics = analytics;
    }

    @NonNull
    @Override
    public AnalyticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analytics, parent, false);
        return new AnalyticsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnalyticsViewHolder holder, int position) {
        SalesAnalytics data = analytics.get(position);
        holder.tvMonth.setText(data.getMonth());
        holder.tvSales.setText("$" + String.format("%.2f", data.getTotalSales()));
        holder.tvEnrollments.setText(data.getEnrollmentCount() + " enrollments");
    }

    @Override
    public int getItemCount() {
        return analytics.size();
    }

    static class AnalyticsViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonth, tvSales, tvEnrollments;

        public AnalyticsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tv_ana_month);
            tvSales = itemView.findViewById(R.id.tv_ana_sales);
            tvEnrollments = itemView.findViewById(R.id.tv_ana_enrollments);
        }
    }
}