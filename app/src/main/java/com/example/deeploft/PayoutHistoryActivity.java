package com.example.deeploft;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.adapters.PayoutAdapter;
import com.example.deeploft.models.PayoutTransaction;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayoutHistoryActivity extends AppCompatActivity {
    private final List<PayoutTransaction> payoutList = new ArrayList<>();
    private PayoutAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout_history);

        Toolbar toolbar = findViewById(R.id.toolbar_payouts);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rv_payout_history);
        adapter = new PayoutAdapter(payoutList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        fetchPayouts();
    }

    private void fetchPayouts() {
        SessionManager sessionManager = new SessionManager(this);
        String name = sessionManager.getName();
        if ("ashan@deeploft.com".equals(sessionManager.getEmail())) {
            name = "DeepLoft Admin";
        }

        RetrofitClient.getApiService().getPayouts(name).enqueue(new Callback<List<PayoutTransaction>>() {
            @Override
            public void onResponse(@NonNull Call<List<PayoutTransaction>> call, @NonNull Response<List<PayoutTransaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    payoutList.clear();
                    payoutList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PayoutTransaction>> call, @NonNull Throwable t) {
                Toast.makeText(PayoutHistoryActivity.this, "Error loading payout history", Toast.LENGTH_SHORT).show();
            }
        });
    }
}