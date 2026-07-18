package com.example.deeploft.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.R;
import com.example.deeploft.models.WithdrawalRequest;
import java.util.List;

public class WithdrawalRequestAdapter extends RecyclerView.Adapter<WithdrawalRequestAdapter.ViewHolder> {

    private final List<WithdrawalRequest> requests;
    private final OnWithdrawalActionListener listener;

    public interface OnWithdrawalActionListener {
        void onApprove(WithdrawalRequest request);
        void onReject(WithdrawalRequest request);
    }

    public WithdrawalRequestAdapter(List<WithdrawalRequest> requests, OnWithdrawalActionListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_withdrawal_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WithdrawalRequest request = requests.get(position);
        holder.tvInstructor.setText(request.getInstructorName());
        holder.tvAmount.setText("$" + String.format("%.2f", request.getAmount()));
        holder.tvBank.setText(request.getBankName() + " - " + request.getBankAccountNumber());

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(request));
        holder.btnReject.setOnClickListener(v -> listener.onReject(request));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInstructor, tvAmount, tvBank;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInstructor = itemView.findViewById(R.id.tv_withdraw_instructor);
            tvAmount = itemView.findViewById(R.id.tv_withdraw_amount);
            tvBank = itemView.findViewById(R.id.tv_withdraw_bank);
            btnApprove = itemView.findViewById(R.id.btn_approve_withdraw);
            btnReject = itemView.findViewById(R.id.btn_reject_withdraw);
        }
    }
}