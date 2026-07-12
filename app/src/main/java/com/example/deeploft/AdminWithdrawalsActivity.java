package com.example.deeploft;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.adapters.WithdrawalRequestAdapter;
import com.example.deeploft.models.WithdrawalRequest;
import com.example.deeploft.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminWithdrawalsActivity extends AppCompatActivity {
    private final List<WithdrawalRequest> requestList = new ArrayList<>();
    private WithdrawalRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_withdrawals);

        Toolbar toolbar = findViewById(R.id.toolbar_withdrawals);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rv_admin_withdrawals);
        adapter = new WithdrawalRequestAdapter(requestList, new WithdrawalRequestAdapter.OnWithdrawalActionListener() {
            @Override
            public void onApprove(WithdrawalRequest request) {
                confirmAction(request, "APPROVED");
            }

            @Override
            public void onReject(WithdrawalRequest request) {
                confirmAction(request, "REJECTED");
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        fetchPendingRequests();
    }

    private void confirmAction(WithdrawalRequest request, String status) {
        new AlertDialog.Builder(this)
                .setTitle(status + " Withdrawal")
                .setMessage("Are you sure you want to " + status.toLowerCase() + " this request for $" + request.getAmount() + "?")
                .setPositiveButton("Confirm", (dialog, which) -> updateStatus(request, status))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateStatus(WithdrawalRequest request, String status) {
        RetrofitClient.getApiService().updateWithdrawalStatus(request.getId(), status, "Processed by Admin").enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<WithdrawalRequest> call, @NonNull Response<WithdrawalRequest> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminWithdrawalsActivity.this, "Request " + status.toLowerCase(), Toast.LENGTH_SHORT).show();
                    fetchPendingRequests();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WithdrawalRequest> call, @NonNull Throwable t) {}
        });
    }

    private void fetchPendingRequests() {
        RetrofitClient.getApiService().getPendingWithdrawals().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<WithdrawalRequest>> call, @NonNull Response<List<WithdrawalRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    requestList.clear();
                    requestList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WithdrawalRequest>> call, @NonNull Throwable t) {}
        });
    }
}