package com.example.deeploft;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.deeploft.models.Question;
import com.example.deeploft.models.Quiz;
import com.example.deeploft.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity {
    private TextView tvQuestion;
    private RadioGroup rgOptions;
    private Button btnNext;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Toolbar toolbar = findViewById(R.id.toolbar_quiz);
        toolbar.setNavigationOnClickListener(v -> finish());

        Long courseId = getIntent().getLongExtra("COURSE_ID", -1);
        
        tvQuestion = findViewById(R.id.tv_question_text);
        rgOptions = findViewById(R.id.rg_options);
        btnNext = findViewById(R.id.btn_next_question);

        if (courseId != -1) {
            fetchQuiz(courseId);
        }

        btnNext.setOnClickListener(v -> checkAnswerAndNext());
    }

    private void fetchQuiz(Long courseId) {
        RetrofitClient.getApiService().getQuizByCourse(courseId).enqueue(new Callback<Quiz>() {
            @Override
            public void onResponse(@NonNull Call<Quiz> call, @NonNull Response<Quiz> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questions = response.body().getQuestions();
                    if (questions != null && !questions.isEmpty()) {
                        displayQuestion();
                    } else {
                        tvQuestion.setText("No quiz available for this course.");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Quiz> call, @NonNull Throwable t) {
                Toast.makeText(QuizActivity.this, "Failed to load quiz", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayQuestion() {
        Question q = questions.get(currentQuestionIndex);
        tvQuestion.setText(q.getQuestionText());
        rgOptions.removeAllViews();
        
        List<String> options = q.getOptions();
        for (int i = 0; i < options.size(); i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(options.get(i));
            rb.setId(i);
            rgOptions.addView(rb);
        }
    }

    private void checkAnswerAndNext() {
        int selectedId = rgOptions.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedId == questions.get(currentQuestionIndex).getCorrectOptionIndex()) {
            score++;
        }

        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            displayQuestion();
        } else {
            Toast.makeText(this, "Quiz finished! Your score: " + score + "/" + questions.size(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
}