package com.example.deeploft.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.R;
import com.example.deeploft.models.PayoutTransaction;
import java.util.List;

public class PayoutAdapter extends RecyclerView.Adapter<PayoutAdapter.ViewHolder> {

    private final List<PayoutTransaction> transactions;

    public PayoutAdapter(List<PayoutTransaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payout_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PayoutTransaction tx = transactions.get(position);
        holder.tvRef.setText(tx.getReference());
        holder.tvAmount.setText("$" + String.format("%.2f", tx.getAmount()));
        holder.tvBank.setText(tx.getBankName() + " - " + tx.getBankAccountNumber());
        holder.tvStatus.setText(tx.getStatus());
        holder.tvTime.setText(tx.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRef, tvAmount, tvBank, tvStatus, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRef = itemView.findViewById(R.id.tv_payout_ref);
            tvAmount = itemView.findViewById(R.id.tv_payout_amount);
            tvBank = itemView.findViewById(R.id.tv_payout_bank);
            tvStatus = itemView.findViewById(R.id.tv_payout_status);
            tvTime = itemView.findViewById(R.id.tv_payout_time);
        }
    }
}