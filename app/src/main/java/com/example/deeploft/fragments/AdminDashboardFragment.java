package com.example.deeploft.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.deeploft.ManagementListActivity;
import com.example.deeploft.R;
import com.example.deeploft.models.PlatformSettings;
import com.example.deeploft.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardFragment extends Fragment {
    private TextView tvTotalCommission;
    private EditText etBankName, etBankAccount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_admin_dashboard, container, false);

        tvTotalCommission = view.findViewById(R.id.tv_admin_total_commission);
        etBankName = view.findViewById(R.id.et_admin_bank_name);
        etBankAccount = view.findViewById(R.id.et_admin_bank_account);
        Button btnSave = view.findViewById(R.id.btn_save_admin_settings);
        Button btnRevenue = view.findViewById(R.id.btn_admin_view_revenue);

        Button btnCourses = view.findViewById(R.id.btn_admin_view_courses);
        Button btnReviewQueue = view.findViewById(R.id.btn_admin_review_queue);
        Button btnStudents = view.findViewById(R.id.btn_admin_view_students);
        Button btnInstructors = view.findViewById(R.id.btn_admin_view_instructors);
        Button btnPayoutHistory = view.findViewById(R.id.btn_admin_view_payout_history);
        Button btnWithdrawals = view.findViewById(R.id.btn_admin_view_withdrawals);

        fetchAdminData();
        btnSave.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Update Details")
                    .setMessage("Do you want to save the changes to your payout details?")
                    .setPositiveButton("Update", (dialog, which) -> saveAdminDetails())
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnRevenue.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), com.example.deeploft.AdminRevenueActivity.class));
        });

        btnCourses.setOnClickListener(v -> openManagement("COURSES"));
        btnReviewQueue.setOnClickListener(v -> openManagement("APPROVAL_QUEUE"));
        btnStudents.setOnClickListener(v -> openManagement("STUDENTS"));
        btnInstructors.setOnClickListener(v -> openManagement("INSTRUCTORS"));
        btnPayoutHistory.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), com.example.deeploft.PayoutHistoryActivity.class));
        });
        btnWithdrawals.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), com.example.deeploft.AdminWithdrawalsActivity.class));
        });

        return view;
    }

    private void openManagement(String type) {
        Intent intent = new Intent(getActivity(), ManagementListActivity.class);
        intent.putExtra("MANAGEMENT_TYPE", type);
        startActivity(intent);
    }

    private void fetchAdminData() {
        RetrofitClient.getApiService().getTotalPlatformCommission().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Double> call, @NonNull Response<Double> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvTotalCommission.setText("$" + String.format("%.2f", response.body()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Double> call, @NonNull Throwable t) { }
        });

        RetrofitClient.getApiService().getPlatformSettings().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PlatformSettings> call, @NonNull Response<PlatformSettings> response) {
                if (response.isSuccessful() && response.body() != null) {
                    etBankName.setText(response.body().getOwnerBankName());
                    etBankAccount.setText(response.body().getOwnerBankAccount());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlatformSettings> call, @NonNull Throwable t) { }
        });
    }

    private void saveAdminDetails() {
        String bank = etBankName.getText().toString().trim();
        String account = etBankAccount.getText().toString().trim();

        PlatformSettings settings = new PlatformSettings();
        settings.setOwnerBankName(bank);
        settings.setOwnerBankAccount(account);
        settings.setCommissionPercentage(20.0);

        RetrofitClient.getApiService().updatePlatformSettings(settings).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PlatformSettings> call, @NonNull Response<PlatformSettings> response) {
                if (response.isSuccessful()) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Admin payout details updated!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlatformSettings> call, @NonNull Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}