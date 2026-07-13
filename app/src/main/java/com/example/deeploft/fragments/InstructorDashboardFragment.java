package com.example.deeploft.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.AnalyticsActivity;
import com.example.deeploft.CourseContentActivity;
import com.example.deeploft.CreateCourseActivity;
import com.example.deeploft.InstructorStudentProgressActivity;
import com.example.deeploft.PayoutHistoryActivity;
import com.example.deeploft.PayoutSettingsActivity;
import com.example.deeploft.R;
import com.example.deeploft.adapters.ActivityLogAdapter;
import com.example.deeploft.adapters.InstructorCourseAdapter;
import com.example.deeploft.models.ActivityLog;
import com.example.deeploft.models.Course;
import com.example.deeploft.models.User;
import com.example.deeploft.models.WithdrawalRequest;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstructorDashboardFragment extends Fragment {
    private TextView tvEarnings, tvNoCoursesMsg, tvNoActivityMsg, tvWithdrawable;
    private double currentBalance = 0;
    private final List<Course> myCourses = new ArrayList<>();
    private final List<ActivityLog> activityLogs = new ArrayList<>();
    private InstructorCourseAdapter courseAdapter;
    private ActivityLogAdapter logAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instructor_dashboard, container, false);

        tvEarnings = view.findViewById(R.id.tv_total_earnings);
        tvWithdrawable = view.findViewById(R.id.tv_withdrawable_balance);
        tvNoCoursesMsg = view.findViewById(R.id.tv_no_courses_msg);
        tvNoActivityMsg = view.findViewById(R.id.tv_no_activity_msg);
        
        Button btnAddCourse = view.findViewById(R.id.btn_add_course);
        View btnPayout = view.findViewById(R.id.btn_payout_settings);
        View btnAnalytics = view.findViewById(R.id.btn_view_analytics_card);
        View btnStudentProgress = view.findViewById(R.id.btn_view_student_progress_card);
        View btnPayoutHistory = view.findViewById(R.id.btn_view_payout_history_card);
        View btnMessages = view.findViewById(R.id.btn_instructor_messages_card);
        Button btnWithdraw = view.findViewById(R.id.btn_withdraw_funds);
        RecyclerView rvCourses = view.findViewById(R.id.rv_instructor_courses);
        RecyclerView rvLogs = view.findViewById(R.id.rv_activity_logs);
        
        courseAdapter = new InstructorCourseAdapter(myCourses, new InstructorCourseAdapter.OnCourseActionListener() {
            @Override
            public void onEdit(Course course) {
                Intent intent = new Intent(getActivity(), CreateCourseActivity.class);
                intent.putExtra("EDIT_MODE", true);
                intent.putExtra("COURSE_OBJECT", course);
                startActivity(intent);
            }

            @Override
            public void onPreview(Course course) {
                Intent intent = new Intent(getActivity(), CourseContentActivity.class);
                intent.putExtra("COURSE_OBJECT", course);
                startActivity(intent);
            }
        });
        rvCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCourses.setAdapter(courseAdapter);

        logAdapter = new ActivityLogAdapter(activityLogs);
        rvLogs.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLogs.setAdapter(logAdapter);

        btnAddCourse.setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), CreateCourseActivity.class))
        );

        btnPayout.setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), PayoutSettingsActivity.class))
        );

        btnAnalytics.setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), AnalyticsActivity.class))
        );

        btnStudentProgress.setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), InstructorStudentProgressActivity.class))
        );

        btnPayoutHistory.setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), PayoutHistoryActivity.class))
        );

        btnMessages.setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), com.example.deeploft.MessageListActivity.class))
        );

        btnWithdraw.setOnClickListener(v -> {
            if (currentBalance > 0) {
                showWithdrawalDialog();
            } else {
                Toast.makeText(getContext(), "You have no funds available for withdrawal yet.", Toast.LENGTH_LONG).show();
            }
        });

        // Add visual feedback for card clicks
        setupCardFeedback(btnAnalytics);
        setupCardFeedback(btnStudentProgress);
        setupCardFeedback(btnPayoutHistory);
        setupCardFeedback(btnMessages);

        return view;
    }

    private void setupCardFeedback(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.7f);
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1.0f);
                    break;
            }
            return false;
        });
    }

    private void showWithdrawalDialog() {
        if (currentBalance <= 0) {
            Toast.makeText(getContext(), "You don't have any withdrawable balance.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Request Withdrawal");
        builder.setMessage("Your current balance is $" + String.format("%.2f", currentBalance));

        final EditText input = new EditText(getContext());
        input.setHint("Enter amount to withdraw");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setPositiveButton("Request", (dialog, which) -> {
            String val = input.getText().toString().trim();
            if (!val.isEmpty()) {
                double amount = Double.parseDouble(val);
                if (amount > 0 && amount <= currentBalance) {
                    processWithdrawalRequest(amount);
                } else {
                    Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void processWithdrawalRequest(double amount) {
        SessionManager sm = new SessionManager(requireContext());
        
        // Fetch current user details for bank info
        RetrofitClient.getApiService().getUser(sm.getEmail()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    WithdrawalRequest wr = new WithdrawalRequest();
                    wr.setInstructorName(user.getName());
                    wr.setInstructorEmail(user.getEmail());
                    wr.setAmount(amount);
                    wr.setBankName(user.getBankName());
                    wr.setBankAccountNumber(user.getBankAccountNumber());

                    RetrofitClient.getApiService().createWithdrawalRequest(wr).enqueue(new Callback<WithdrawalRequest>() {
                        @Override
                        public void onResponse(@NonNull Call<WithdrawalRequest> call, @NonNull Response<WithdrawalRequest> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Withdrawal request sent to Admin!", Toast.LENGTH_LONG).show();
                                fetchInstructorData(); // Refresh balance
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<WithdrawalRequest> call, @NonNull Throwable t) {}
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchInstructorData();
        fetchActivityLogs();
    }

    private void fetchInstructorData() {
        SessionManager sessionManager = new SessionManager(requireContext());
        String instructorName = sessionManager.getName();

        RetrofitClient.getApiService().getCourses(null, null, null, null).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    myCourses.clear();
                    for (Course course : response.body()) {
                        if (instructorName != null && instructorName.equalsIgnoreCase(course.getInstructor())) {
                            myCourses.add(course);
                        }
                    }
                    
                    fetchRealBalance(instructorName);

                    if (myCourses.isEmpty()) {
                        tvNoCoursesMsg.setVisibility(View.VISIBLE);
                        if (getContext() != null) Toast.makeText(getContext(), "No courses found for " + instructorName, Toast.LENGTH_SHORT).show();
                    } else {
                        tvNoCoursesMsg.setVisibility(View.GONE);
                    }
                    
                    courseAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchRealBalance(String name) {
        RetrofitClient.getApiService().getInstructorBalance(name).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Double>> call, @NonNull Response<Map<String, Double>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentBalance = response.body().get("balance");
                    tvWithdrawable.setText(getString(R.string.available_payout, String.format("%.2f", currentBalance)));
                    tvEarnings.setText("$" + String.format("%.2f", currentBalance));
                    if (getContext() != null) Toast.makeText(getContext(), "Balance loaded: $" + currentBalance, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Double>> call, @NonNull Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to fetch balance", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchActivityLogs() {
        SessionManager sessionManager = new SessionManager(requireContext());
        RetrofitClient.getApiService().getActivityLogs(sessionManager.getName()).enqueue(new Callback<List<ActivityLog>>() {
            @Override
            public void onResponse(@NonNull Call<List<ActivityLog>> call, @NonNull Response<List<ActivityLog>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activityLogs.clear();
                    activityLogs.addAll(response.body());
                    
                    if (activityLogs.isEmpty()) {
                        tvNoActivityMsg.setVisibility(View.VISIBLE);
                    } else {
                        tvNoActivityMsg.setVisibility(View.GONE);
                    }
                    
                    logAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ActivityLog>> call, @NonNull Throwable t) { }
        });
    }
}