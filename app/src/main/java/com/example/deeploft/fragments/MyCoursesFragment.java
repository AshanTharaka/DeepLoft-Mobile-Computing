package com.example.deeploft.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deeploft.CourseContentActivity;
import com.example.deeploft.R;
import com.example.deeploft.adapters.CourseAdapter;
import com.example.deeploft.models.Course;
import com.example.deeploft.models.Enrollment;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCoursesFragment extends Fragment {
    private CourseAdapter adapter;
    private final List<Course> enrolledCourses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_courses, container, false);

        RecyclerView rvMyCourses = view.findViewById(R.id.rv_my_courses);
        rvMyCourses.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CourseAdapter(enrolledCourses, course -> {
            Intent intent = new Intent(getActivity(), CourseContentActivity.class);
            intent.putExtra("COURSE_OBJECT", course);
            startActivity(intent);
        });
        rvMyCourses.setAdapter(adapter);

        fetchEnrolledCourses();

        return view;
    }

    private void fetchEnrolledCourses() {
        SessionManager sessionManager = new SessionManager(requireContext());
        RetrofitClient.getApiService().getMyCourses(sessionManager.getEmail()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Enrollment>> call, @NonNull Response<List<Enrollment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int oldSize = enrolledCourses.size();
                    enrolledCourses.clear();
                    adapter.notifyItemRangeRemoved(0, oldSize);
                    
                    for (Enrollment enrollment : response.body()) {
                        enrolledCourses.add(enrollment.getCourse());
                    }
                    adapter.notifyItemRangeInserted(0, enrolledCourses.size());
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