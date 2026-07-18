package com.example.deeploft.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deeploft.R;
import com.example.deeploft.adapters.CourseAdapter;
import com.example.deeploft.models.Course;
import com.example.deeploft.network.RetrofitClient;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private CourseAdapter adapter;
    private final List<Course> filteredCourses = new ArrayList<>();
    private EditText etSearch;
    private String currentCategory = "";
    private Boolean freeOnly = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etSearch = view.findViewById(R.id.et_search);
        ImageButton btnAiSearch = view.findViewById(R.id.btn_ai_search);
        ChipGroup cgCategories = view.findViewById(R.id.cg_categories);
        ChipGroup cgPrice = view.findViewById(R.id.cg_price_filter);
        RecyclerView rvSearch = view.findViewById(R.id.rv_search_results);
        rvSearch.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CourseAdapter(filteredCourses);
        rvSearch.setAdapter(adapter);

        btnAiSearch.setOnClickListener(v -> {
            String intent = etSearch.getText().toString().trim();
            if (!intent.isEmpty()) {
                performAiSearch(intent);
            } else {
                Toast.makeText(getContext(), "Describe what you want to learn first!", Toast.LENGTH_SHORT).show();
            }
        });

        cgCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentCategory = "";
            } else {
                Chip chip = group.findViewById(checkedIds.get(0));
                String category = chip.getText().toString();
                currentCategory = category.equals("All") ? "" : category;
            }
            applyFilters();
        });

        cgPrice.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                freeOnly = null;
            } else {
                Chip chip = group.findViewById(checkedIds.get(0));
                String priceType = chip.getText().toString();
                if (priceType.equals("Free")) freeOnly = true;
                else if (priceType.equals("Paid")) freeOnly = false;
                else freeOnly = null;
            }
            applyFilters();
        });

        applyFilters();

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

        return view;
    }

    private void applyFilters() {
        String query = etSearch.getText().toString().trim();
        // Combining text search with category search for backend
        String finalQuery = currentCategory.isEmpty() ? query : currentCategory;
        fetchAllCourses(finalQuery, freeOnly);
    }

    private void fetchAllCourses(String query, Boolean freeOnly) {
        RetrofitClient.getApiService().getCourses(query, null, freeOnly, "PUBLISHED").enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    filteredCourses.clear();
                    filteredCourses.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading courses", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void performAiSearch(String intent) {
        Toast.makeText(getContext(), "AI is analyzing your learning path...", Toast.LENGTH_SHORT).show();
        RetrofitClient.getApiService().aiCourseSearch(intent).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    filteredCourses.clear();
                    filteredCourses.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    if (response.body().isEmpty()) {
                        Toast.makeText(getContext(), "AI couldn't find a direct match. Try different words!", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "AI Search error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}