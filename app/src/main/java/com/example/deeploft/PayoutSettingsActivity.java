package com.example.deeploft;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.deeploft.models.User;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayoutSettingsActivity extends AppCompatActivity {
    private EditText etBankName, etAccountNumber;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout_settings);

        Toolbar toolbar = findViewById(R.id.toolbar_payout_settings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        sessionManager = new SessionManager(this);
        etBankName = findViewById(R.id.et_bank_name);
        etAccountNumber = findViewById(R.id.et_bank_account);
        MaterialButton btnSave = findViewById(R.id.btn_save_bank_details);

        loadCurrentDetails();

        btnSave.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Save Details")
                    .setMessage("Are you sure you want to update your payout bank details?")
                    .setPositiveButton("Save", (dialog, which) -> saveBankDetails())
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadCurrentDetails() {
        RetrofitClient.getApiService().getUser(sessionManager.getEmail()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    if (user.getBankName() != null) etBankName.setText(user.getBankName());
                    if (user.getBankAccountNumber() != null) etAccountNumber.setText(user.getBankAccountNumber());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(PayoutSettingsActivity.this, "Failed to load current settings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveBankDetails() {
        String bankName = etBankName.getText().toString().trim();
        String accountNumber = etAccountNumber.getText().toString().trim();

        if (bankName.isEmpty() || accountNumber.isEmpty()) {
            Toast.makeText(this, "Please fill all bank details", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(sessionManager.getName(), sessionManager.getEmail(), null, sessionManager.getRole());
        user.setBankName(bankName);
        user.setBankAccountNumber(accountNumber);

        RetrofitClient.getApiService().updateProfile(user).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PayoutSettingsActivity.this, "Payout details updated successfully!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(PayoutSettingsActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(PayoutSettingsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
