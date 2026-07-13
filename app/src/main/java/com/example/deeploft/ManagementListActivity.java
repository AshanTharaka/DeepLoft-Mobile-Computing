package com.example.deeploft;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.adapters.CourseApprovalAdapter;
import com.example.deeploft.adapters.CourseManagementAdapter;
import com.example.deeploft.adapters.UserAdapter;
import com.example.deeploft.models.Course;
import com.example.deeploft.models.User;
import com.example.deeploft.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagementListActivity extends AppCompatActivity {
    private RecyclerView rv;
    private String type; // "COURSES", "STUDENTS", "INSTRUCTORS", "APPROVAL_QUEUE"
    private final List<Course> courseList = new ArrayList<>();
    private final List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_list);

        type = getIntent().getStringExtra("MANAGEMENT_TYPE");
        
        Toolbar toolbar = findViewById(R.id.toolbar_management);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage " + type.replace("_", " "));
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rv = findViewById(R.id.rv_management_list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        fetchData();
    }

    private void fetchData() {
        if ("COURSES".equals(type)) {
            fetchCourses(null);
        } else if ("APPROVAL_QUEUE".equals(type)) {
            fetchCourses("PENDING_APPROVAL");
        } else if ("STUDENTS".equals(type)) {
            fetchUsers("STUDENT");
        } else if ("INSTRUCTORS".equals(type)) {
            fetchUsers("INSTRUCTOR");
        }
    }

    private void fetchCourses(String status) {
        RetrofitClient.getApiService().getCourses(null, null, null, status).enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    courseList.clear();
                    courseList.addAll(response.body());
                    
                    if ("APPROVAL_QUEUE".equals(type)) {
                        CourseApprovalAdapter adapter = new CourseApprovalAdapter(courseList, new CourseApprovalAdapter.OnCourseApprovalListener() {
                            @Override
                            public void onApprove(Course course) {
                                approveCourse(course, "PUBLISHED");
                            }

                            @Override
                            public void onReject(Course course) {
                                showRejectDialog(course);
                            }

                            @Override
                            public void onPreview(Course course) {
                                Intent intent = new Intent(ManagementListActivity.this, CourseContentActivity.class);
                                intent.putExtra("COURSE_OBJECT", course);
                                startActivity(intent);
                            }
                        });
                        rv.setAdapter(adapter);
                    } else {
                        CourseManagementAdapter adapter = new CourseManagementAdapter(courseList, new CourseManagementAdapter.OnCourseActionListener() {
                            @Override
                            public void onDelete(Course course) {
                                deleteCourse(course);
                            }

                            @Override
                            public void onPreview(Course course) {
                                Intent intent = new Intent(ManagementListActivity.this, CourseContentActivity.class);
                                intent.putExtra("COURSE_OBJECT", course);
                                startActivity(intent);
                            }
                        });
                        rv.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable t) {
                Toast.makeText(ManagementListActivity.this, "Error loading courses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRejectDialog(Course course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reject Course");
        final EditText input = new EditText(this);
        input.setHint("Reason for rejection...");
        builder.setView(input);
        builder.setPositiveButton("Reject", (dialog, which) -> {
            String comment = input.getText().toString().trim();
            approveCourse(course, "REJECTED", comment);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void approveCourse(Course course, String status) {
        approveCourse(course, status, "");
    }

    private void approveCourse(Course course, String status, String comment) {
        RetrofitClient.getApiService().reviewCourse(course.getId(), status, comment).enqueue(new Callback<Course>() {
            @Override
            public void onResponse(@NonNull Call<Course> call, @NonNull Response<Course> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManagementListActivity.this, "Course " + status.toLowerCase(), Toast.LENGTH_SHORT).show();
                    fetchData();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Course> call, @NonNull Throwable t) {}
        });
    }

    private void fetchUsers(String role) {
        RetrofitClient.getApiService().getUsersByRole(role).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body());
                    UserAdapter adapter = new UserAdapter(userList, user -> deleteUser(user));
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                Toast.makeText(ManagementListActivity.this, "Error loading users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCourse(Course course) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to permanently remove '" + course.getTitle() + "' from the platform?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    RetrofitClient.getApiService().deleteCourse(course.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ManagementListActivity.this, "Course deleted", Toast.LENGTH_SHORT).show();
                                fetchData();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast.makeText(ManagementListActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteUser(User user) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to permanently delete user '" + user.getEmail() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    RetrofitClient.getApiService().deleteUser(user.getEmail()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ManagementListActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                                fetchData();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast.makeText(ManagementListActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}