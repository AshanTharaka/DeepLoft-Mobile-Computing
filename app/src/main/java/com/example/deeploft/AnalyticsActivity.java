package com.example.deeploft;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.adapters.AnalyticsAdapter;
import com.example.deeploft.models.SalesAnalytics;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalyticsActivity extends AppCompatActivity {
    private final List<SalesAnalytics> analyticsList = new ArrayList<>();
    private AnalyticsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        Toolbar toolbar = findViewById(R.id.toolbar_analytics);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rv_monthly_analytics);
        adapter = new AnalyticsAdapter(analyticsList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        fetchAnalytics();
    }

    private void fetchAnalytics() {
        SessionManager sessionManager = new SessionManager(this);
        RetrofitClient.getApiService().getInstructorAnalytics(sessionManager.getName()).enqueue(new Callback<List<SalesAnalytics>>() {
            @Override
            public void onResponse(@NonNull Call<List<SalesAnalytics>> call, @NonNull Response<List<SalesAnalytics>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    analyticsList.clear();
                    analyticsList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SalesAnalytics>> call, @NonNull Throwable t) {
                Toast.makeText(AnalyticsActivity.this, "Error loading analytics", Toast.LENGTH_SHORT).show();
            }
        });
    }
}