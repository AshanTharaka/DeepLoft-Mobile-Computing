package com.example.deeploft;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.adapters.AnalyticsAdapter;
import com.example.deeploft.models.PlatformRevenue;
import com.example.deeploft.models.SalesAnalytics;
import com.example.deeploft.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRevenueActivity extends AppCompatActivity {
    private TextView tvCommission, tvGross, tvPayouts, tvEnrollments;
    private final List<SalesAnalytics> monthlyData = new ArrayList<>();
    private AnalyticsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_revenue);

        tvCommission = findViewById(R.id.tv_admin_total_commission);
        tvGross = findViewById(R.id.tv_admin_gross_revenue);
        tvPayouts = findViewById(R.id.tv_admin_instructor_payouts);
        tvEnrollments = findViewById(R.id.tv_admin_total_enrollments);
        Button btnBack = findViewById(R.id.btn_revenue_back);

        RecyclerView rv = findViewById(R.id.rv_admin_monthly_revenue);
        adapter = new AnalyticsAdapter(monthlyData);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        fetchRevenueData();

        btnBack.setOnClickListener(v -> finish());
    }

    private void fetchRevenueData() {
        RetrofitClient.getApiService().getPlatformRevenue().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PlatformRevenue> call, @NonNull Response<PlatformRevenue> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PlatformRevenue rev = response.body();
                    tvCommission.setText("$" + String.format("%.2f", rev.getTotalCommission()));
                    tvGross.setText("$" + String.format("%.2f", rev.getTotalRevenue()));
                    tvPayouts.setText("$" + String.format("%.2f", rev.getTotalInstructorPayouts()));
                    tvEnrollments.setText(String.valueOf(rev.getTotalEnrollments()));

                    monthlyData.clear();
                    if (rev.getMonthlyRevenue() != null) {
                        monthlyData.addAll(rev.getMonthlyRevenue());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlatformRevenue> call, @NonNull Throwable t) {
                Toast.makeText(AdminRevenueActivity.this, "Error loading revenue data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}