package com.example.deeploft;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.deeploft.models.StudyPlan;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudyPlanActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_plan);

        Toolbar toolbar = findViewById(R.id.toolbar_study_plan);
        toolbar.setNavigationOnClickListener(v -> finish());

        String courseTitle = getIntent().getStringExtra("COURSE_TITLE");
        SessionManager sessionManager = new SessionManager(this);

        TextView tvCourse = findViewById(R.id.tv_plan_course);
        TextView tvGoal = findViewById(R.id.tv_plan_goal);
        TextView tvTasks = findViewById(R.id.tv_plan_tasks);
        Button btnClose = findViewById(R.id.btn_plan_close);

        if (courseTitle != null) tvCourse.setText("Study Plan: " + courseTitle);

        fetchStudyPlan(sessionManager.getEmail(), courseTitle, tvGoal, tvTasks);

        btnClose.setOnClickListener(v -> finish());
    }

    private void fetchStudyPlan(String email, String courseTitle, TextView tvGoal, TextView tvTasks) {
        RetrofitClient.getApiService().getStudyPlan(email, courseTitle).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<StudyPlan> call, @NonNull Response<StudyPlan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StudyPlan plan = response.body();
                    tvGoal.setText("Goal: " + plan.getGoal());
                    
                    StringBuilder tasksStr = new StringBuilder();
                    if (plan.getDailyTasks() != null) {
                        for (String task : plan.getDailyTasks()) {
                            tasksStr.append("• ").append(task).append("\n\n");
                        }
                    }
                    tvTasks.setText(tasksStr.toString());
                } else {
                    tvTasks.setText("No study plan generated yet. Ask the AI to create one!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudyPlan> call, @NonNull Throwable t) {
                Toast.makeText(StudyPlanActivity.this, "Error loading plan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}