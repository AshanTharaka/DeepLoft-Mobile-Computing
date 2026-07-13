package com.example.deeploft;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.deeploft.models.PlatformSettings;
import com.example.deeploft.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {
    private TextView tvTotalCommission;
    private EditText etBankName, etBankAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvTotalCommission = findViewById(R.id.tv_admin_total_commission);
        etBankName = findViewById(R.id.et_admin_bank_name);
        etBankAccount = findViewById(R.id.et_admin_bank_account);
        Button btnSave = findViewById(R.id.btn_save_admin_settings);

        findViewById(R.id.btn_admin_view_courses).setOnClickListener(v -> startManagement("COURSES"));
        findViewById(R.id.btn_admin_review_queue).setOnClickListener(v -> startManagement("APPROVAL_QUEUE"));
        findViewById(R.id.btn_admin_view_students).setOnClickListener(v -> startManagement("STUDENTS"));
        findViewById(R.id.btn_admin_view_instructors).setOnClickListener(v -> startManagement("INSTRUCTORS"));
        
        findViewById(R.id.btn_admin_view_revenue).setOnClickListener(v -> 
            startActivity(new android.content.Intent(this, AdminRevenueActivity.class)));
        findViewById(R.id.btn_admin_view_payout_history).setOnClickListener(v -> 
            startActivity(new android.content.Intent(this, PayoutHistoryActivity.class)));
        findViewById(R.id.btn_admin_view_withdrawals).setOnClickListener(v -> 
            startActivity(new android.content.Intent(this, AdminWithdrawalsActivity.class)));

        fetchAdminData();
        btnSave.setOnClickListener(v -> saveAdminDetails());
    }

    private void startManagement(String type) {
        android.content.Intent intent = new android.content.Intent(this, ManagementListActivity.class);
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
            public void onFailure(@NonNull Call<Double> call, @NonNull Throwable t) {
                // Error
            }
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
            public void onFailure(@NonNull Call<PlatformSettings> call, @NonNull Throwable t) {
                // Error
            }
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
                    Toast.makeText(AdminDashboardActivity.this, "Admin payout details updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlatformSettings> call, @NonNull Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}