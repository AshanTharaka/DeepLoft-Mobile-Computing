package com.example.deeploft.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deeploft.R;
import com.example.deeploft.adapters.CategoryAdapter;
import com.example.deeploft.adapters.CourseAdapter;
import com.example.deeploft.models.Category;
import com.example.deeploft.models.Course;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private CourseAdapter adapter;
    private CourseAdapter recAdapter;
    private final List<Course> courseList = new ArrayList<>();
    private final List<Course> recList = new ArrayList<>();
    private final List<Category> categoryList = new ArrayList<>();
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private TextView tvCourseListHeader;
    private String selectedCategory = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        sessionManager = new SessionManager(requireContext());
        progressBar = view.findViewById(R.id.pb_home);
        tvCourseListHeader = view.findViewById(R.id.tv_all_courses_header);
        
        setupCategories(view);
        setupRecommendations(view);
        setupAllCourses(view);
        
        fetchCourses(null);
        fetchRecommendations();
        
        return view;
    }

    private void setupCategories(View view) {
        RecyclerView rvCategories = view.findViewById(R.id.rv_categories);
        if (categoryList.isEmpty()) {
            categoryList.addAll(Arrays.asList(
                new Category("All", android.R.drawable.ic_menu_agenda),
                new Category("Development", android.R.drawable.ic_menu_today),
                new Category("Design", android.R.drawable.ic_menu_gallery),
                new Category("Science", android.R.drawable.ic_menu_compass),
                new Category("Business", android.R.drawable.ic_menu_agenda),
                new Category("AI", android.R.drawable.ic_menu_help),
                new Category("Music", android.R.drawable.ic_lock_silent_mode_off),
                new Category("Academic", android.R.drawable.ic_menu_edit)
            ));
        }
        
        CategoryAdapter categoryAdapter = new CategoryAdapter(categoryList, category -> {
            if (category.getName().equals("All")) {
                selectedCategory = null;
            } else {
                selectedCategory = category.getName();
            }
            fetchCourses(selectedCategory);
            updateHeader();
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);
    }

    private void updateHeader() {
        if (tvCourseListHeader != null) {
            if (selectedCategory == null) {
                tvCourseListHeader.setText("All Courses");
            } else {
                tvCourseListHeader.setText(selectedCategory + " Courses");
            }
        }
    }

    private void setupRecommendations(View view) {
        RecyclerView rvRecs = view.findViewById(R.id.rv_recommendations);
        rvRecs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recAdapter = new CourseAdapter(recList, true, null);
        rvRecs.setAdapter(recAdapter);
    }

    private void setupAllCourses(View view) {
        RecyclerView rvCourses = view.findViewById(R.id.rv_courses);
        rvCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CourseAdapter(courseList);
        rvCourses.setAdapter(adapter);
    }

    private void fetchCourses(String category) {
        progressBar.setVisibility(View.VISIBLE);
        // Using strict category filtering now
        RetrofitClient.getApiService().getCourses(null, category, null, "PUBLISHED").enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    int oldSize = courseList.size();
                    courseList.clear();
                    adapter.notifyItemRangeRemoved(0, oldSize);
                    courseList.addAll(response.body());
                    adapter.notifyItemRangeInserted(0, courseList.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading courses", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchRecommendations() {
        RetrofitClient.getApiService().getRecommendations(sessionManager.getEmail()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recList.clear();
                    recList.addAll(response.body());
                    recAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable t) {}
        });
    }
}