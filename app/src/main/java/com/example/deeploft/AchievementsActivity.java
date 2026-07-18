package com.example.deeploft;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.adapters.CertificateShowcaseAdapter;
import com.example.deeploft.models.Certificate;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AchievementsActivity extends AppCompatActivity {
    private final List<Certificate> certList = new ArrayList<>();
    private CertificateShowcaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        Toolbar toolbar = findViewById(R.id.toolbar_achievements);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rv_achievements);
        adapter = new CertificateShowcaseAdapter(certList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        fetchCertificates();
    }

    private void fetchCertificates() {
        SessionManager sessionManager = new SessionManager(this);
        RetrofitClient.getApiService().getCertificates(sessionManager.getEmail()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Certificate>> call, @NonNull Response<List<Certificate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    certList.clear();
                    certList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Certificate>> call, @NonNull Throwable t) {
                Toast.makeText(AchievementsActivity.this, "Error loading achievements", Toast.LENGTH_SHORT).show();
            }
        });
    }
}