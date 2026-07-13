package com.example.deeploft.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deeploft.CourseContentActivity;
import com.example.deeploft.MainActivity;
import com.example.deeploft.R;
import com.example.deeploft.adapters.StudentCourseAdapter;
import com.example.deeploft.models.Enrollment;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentDashboardFragment extends Fragment {
    private StudentCourseAdapter adapter;
    private final List<Enrollment> allEnrollments = new ArrayList<>();
    private final List<Enrollment> filteredEnrollments = new ArrayList<>();
    
    private TextView tvCoursesCount;
    private LinearProgressIndicator overallProgress;
    private LinearLayout layoutEmptyState;
    private EditText etSearch;
    private ChipGroup chipGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_courses, container, false);

        tvCoursesCount = view.findViewById(R.id.tv_courses_count);
        overallProgress = view.findViewById(R.id.progress_overall);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        etSearch = view.findViewById(R.id.et_search_my_courses);
        chipGroup = view.findViewById(R.id.chip_group_status);
        Button btnBrowse = view.findViewById(R.id.btn_browse_courses);

        RecyclerView rvMyCourses = view.findViewById(R.id.rv_my_courses);
        rvMyCourses.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new StudentCourseAdapter(filteredEnrollments, enrollment -> {
            Intent intent = new Intent(getActivity(), CourseContentActivity.class);
            intent.putExtra("COURSE_OBJECT", enrollment.getCourse());
            intent.putExtra("LAST_LESSON_ID", enrollment.getLastWatchedLessonId());
            startActivity(intent);
        });
        rvMyCourses.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> applyFilters());

        btnBrowse.setOnClickListener(v -> {
            // Simplified: Navigate back to home
            if (getActivity() instanceof MainActivity) {
                // This assumes your bottom nav setup handles this
                View home = getActivity().findViewById(R.id.navigation_home);
                if (home != null) home.performClick();
            }
        });

        fetchEnrolledCourses();

        return view;
    }

    private void applyFilters() {
        String query = etSearch.getText().toString().toLowerCase().trim();
        int checkedId = chipGroup.getCheckedChipId();

        List<Enrollment> result = allEnrollments.stream()
                .filter(e -> e.getCourse().getTitle().toLowerCase().contains(query))
                .collect(Collectors.toList());

        // Handling logic for In Progress vs Completed
        if (checkedId == R.id.chip_ongoing) {
            // Mocking ongoing logic
            result = result.stream().filter(e -> e.getLastWatchedLessonId() != null).collect(Collectors.toList());
        } else if (checkedId == R.id.chip_completed) {
            // Mocking completed logic
            result = result.stream().filter(e -> e.getLastWatchedLessonId() == null && allEnrollments.indexOf(e) > 0).collect(Collectors.toList());
        }

        filteredEnrollments.clear();
        filteredEnrollments.addAll(result);
        adapter.notifyDataSetChanged();

        layoutEmptyState.setVisibility(filteredEnrollments.isEmpty() ? View.VISIBLE : View.GONE);
        
        // Update Summary Card
        tvCoursesCount.setText(String.valueOf(allEnrollments.size()));
        if (!allEnrollments.isEmpty()) {
            overallProgress.setProgress(45); // Set a realistic average progress
        } else {
            overallProgress.setProgress(0);
        }
    }

    private void fetchEnrolledCourses() {
        SessionManager sessionManager = new SessionManager(requireContext());
        RetrofitClient.getApiService().getMyCourses(sessionManager.getEmail()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Enrollment>> call, @NonNull Response<List<Enrollment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allEnrollments.clear();
                    allEnrollments.addAll(response.body());
                    applyFilters();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Enrollment>> call, @NonNull Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading your courses", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}