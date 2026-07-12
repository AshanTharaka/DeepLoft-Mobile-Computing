package com.example.deeploft;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.deeploft.models.TeacherStats;
import com.example.deeploft.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherProfileActivity extends AppCompatActivity {
    private TextView tvName, tvStudents, tvRating, tvCourses, tvAiBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        String instructorName = getIntent().getStringExtra("INSTRUCTOR_NAME");

        tvName = findViewById(R.id.tv_teacher_name);
        tvStudents = findViewById(R.id.tv_teacher_students);
        tvRating = findViewById(R.id.tv_teacher_rating);
        tvCourses = findViewById(R.id.tv_teacher_courses);
        tvAiBio = findViewById(R.id.tv_teacher_ai_bio);
        Button btnBack = findViewById(R.id.btn_profile_back);

        if (instructorName != null) {
            tvName.setText(instructorName);
            fetchTeacherStats(instructorName);
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void fetchTeacherStats(String name) {
        RetrofitClient.getApiService().getTeacherStats(name).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TeacherStats> call, @NonNull Response<TeacherStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TeacherStats stats = response.body();
                    tvStudents.setText(String.valueOf(stats.getTotalStudents()));
                    tvRating.setText(String.format("%.1f", stats.getAverageRating()));
                    tvCourses.setText(String.valueOf(stats.getTotalCourses()));
                    tvAiBio.setText(stats.getAiBio());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TeacherStats> call, @NonNull Throwable t) {
                Toast.makeText(TeacherProfileActivity.this, "Error loading stats", Toast.LENGTH_SHORT).show();
            }
        });
    }
}