package com.example.deeploft;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.adapters.StudentProgressAdapter;
import com.example.deeploft.models.StudentProgress;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstructorStudentProgressActivity extends AppCompatActivity {
    private final List<StudentProgress> progressList = new ArrayList<>();
    private StudentProgressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_student_progress);

        Toolbar toolbar = findViewById(R.id.toolbar_progress);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rv_student_progress);
        adapter = new StudentProgressAdapter(progressList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        fetchStudentProgress();
    }

    private void fetchStudentProgress() {
        SessionManager sessionManager = new SessionManager(this);
        RetrofitClient.getApiService().getStudentProgressForInstructor(sessionManager.getName()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<StudentProgress>> call, @NonNull Response<List<StudentProgress>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    progressList.clear();
                    progressList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StudentProgress>> call, @NonNull Throwable t) {
                Toast.makeText(InstructorStudentProgressActivity.this, "Error loading student progress", Toast.LENGTH_SHORT).show();
            }
        });
    }
}