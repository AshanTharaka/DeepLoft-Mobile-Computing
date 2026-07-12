package com.example.deeploft;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deeploft.adapters.ReviewAdapter;
import com.example.deeploft.models.Course;
import com.example.deeploft.models.Review;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseDetailActivity extends AppCompatActivity {

    private Course course;
    private final List<Review> reviewList = new ArrayList<>();
    private ReviewAdapter reviewAdapter;
    private SessionManager sessionManager;

    private final ActivityResultLauncher<Intent> paymentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    finish(); // Close details if enrolled successfully
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        course = (Course) getIntent().getSerializableExtra("COURSE_OBJECT");
        sessionManager = new SessionManager(this);

        TextView tvTitle = findViewById(R.id.tv_detail_title);
        TextView tvInstructor = findViewById(R.id.tv_detail_instructor);
        TextView tvPrice = findViewById(R.id.tv_detail_price);
        TextView tvDescription = findViewById(R.id.tv_detail_description);
        TextView tvAiSummary = findViewById(R.id.tv_detail_ai_summary);
        ImageView ivCourse = findViewById(R.id.iv_detail_image);
        Button btnEnroll = findViewById(R.id.btn_enroll);
        Button btnChat = findViewById(R.id.btn_chat_instructor);

        if (course != null) {
            tvTitle.setText(course.getTitle());
            tvInstructor.setText("by " + course.getInstructor());
            tvInstructor.setOnClickListener(v -> {
                Intent intent = new Intent(this, TeacherProfileActivity.class);
                intent.putExtra("INSTRUCTOR_NAME", course.getInstructor());
                startActivity(intent);
            });
            tvPrice.setText("$" + course.getPrice());
            tvDescription.setText(course.getDescription());
            
            if (course.getCourseAiSummary() != null && !course.getCourseAiSummary().isEmpty()) {
                tvAiSummary.setText(course.getCourseAiSummary());
            } else {
                tvAiSummary.setText("No AI summary available yet.");
            }
            
            if (course.getImageUrl() != null && !course.getImageUrl().isEmpty()) {
                String imageUrl = course.getImageUrl();
                if (!imageUrl.startsWith("http")) {
                    imageUrl = RetrofitClient.getServiceUrl() + "uploads/images/" + imageUrl;
                }
                Glide.with(this).load(imageUrl).into(ivCourse);
            }
        }

        btnEnroll.setOnClickListener(v -> {
            if (course != null) {
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("COURSE_OBJECT", course);
                paymentLauncher.launch(intent);
            }
        });

        btnChat.setOnClickListener(v -> {
            if (course != null && course.getInstructorEmail() != null) {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("RECEIVER_EMAIL", course.getInstructorEmail()); 
                intent.putExtra("RECEIVER_NAME", course.getInstructor());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Instructor contact not available", Toast.LENGTH_SHORT).show();
            }
        });

        setupReviews();
    }

    private void setupReviews() {
        RecyclerView rvReviews = findViewById(R.id.rv_course_reviews);
        reviewAdapter = new ReviewAdapter(reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);

        if (course != null && course.getId() != null) {
            fetchReviews();
        }

        RatingBar rbInput = findViewById(R.id.rb_input_rating);
        EditText etComment = findViewById(R.id.et_input_comment);
        Button btnSubmit = findViewById(R.id.btn_submit_review);

        btnSubmit.setOnClickListener(v -> {
            String comment = etComment.getText().toString().trim();
            int rating = (int) rbInput.getRating();
            if (comment.isEmpty() || rating == 0) {
                Toast.makeText(this, "Please provide rating and comment", Toast.LENGTH_SHORT).show();
                return;
            }
            submitReview(comment, rating);
            etComment.setText("");
            rbInput.setRating(0);
        });
    }

    private void fetchReviews() {
        RetrofitClient.getApiService().getCourseReviews(course.getId()).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(@NonNull Call<List<Review>> call, @NonNull Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reviewList.clear();
                    reviewList.addAll(response.body());
                    reviewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Review>> call, @NonNull Throwable t) {
                Toast.makeText(CourseDetailActivity.this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitReview(String comment, int rating) {
        Review review = new Review(sessionManager.getEmail(), sessionManager.getName(), comment, rating, course.getId());
        RetrofitClient.getApiService().addReview(review).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(@NonNull Call<Review> call, @NonNull Response<Review> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CourseDetailActivity.this, "Review submitted", Toast.LENGTH_SHORT).show();
                    fetchReviews();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Review> call, @NonNull Throwable t) {
                Toast.makeText(CourseDetailActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
            }
        });
    }
}