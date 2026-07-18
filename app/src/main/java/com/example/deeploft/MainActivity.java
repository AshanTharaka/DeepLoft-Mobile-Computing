package com.example.deeploft;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavGraph;
import androidx.navigation.ui.NavigationUI;

import com.example.deeploft.models.Course;
import com.example.deeploft.models.Message;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.NotificationHelper;
import com.example.deeploft.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private int lastKnownCourseCount = -1;
    private int lastKnownMessageCount = -1;
    private Timer monitorTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        NotificationHelper.createNotificationChannel(this);
        requestNotificationPermission();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        startMonitor();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupNavigation();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

            String role = sessionManager.getRole();
            String email = sessionManager.getEmail();

            // Set role-specific menu
            bottomNav.getMenu().clear();
            if (email.equalsIgnoreCase("ashan@deeploft.com")) {
                bottomNav.inflateMenu(R.menu.bottom_nav_admin);
            } else if (role.equalsIgnoreCase("INSTRUCTOR")) {
                bottomNav.inflateMenu(R.menu.bottom_nav_instructor);
            } else {
                bottomNav.inflateMenu(R.menu.bottom_nav_student);
            }

            // Dynamically adjust start destination
            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
            if (email.equalsIgnoreCase("ashan@deeploft.com") || role.equalsIgnoreCase("INSTRUCTOR")) {
                navGraph.setStartDestination(R.id.navigation_my_courses); // Dashboard is start for them
            } else {
                navGraph.setStartDestination(R.id.navigation_home); // Courses is start for students
            }
            navController.setGraph(navGraph);

            NavigationUI.setupWithNavController(bottomNav, navController);
        }
    }

    private void startMonitor() {
        monitorTimer = new Timer();
        monitorTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkNewCourses();
                checkNewMessages();
            }
        }, 10000, 30000); 
    }

    private void checkNewCourses() {
        RetrofitClient.getApiService().getCourses(null, null, null, "PUBLISHED").enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().size();
                    if (lastKnownCourseCount != -1 && count > lastKnownCourseCount) {
                        Course latest = response.body().get(count - 1);
                        NotificationHelper.showNotification(MainActivity.this, 
                            "New Course Available!", 
                            "Check out '" + latest.getTitle() + "' by " + latest.getInstructor());
                    }
                    lastKnownCourseCount = count;
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable t) {}
        });
    }

    private void checkNewMessages() {
        // For demo, poll messages from a fixed instructor
        RetrofitClient.getApiService().getChat(sessionManager.getEmail(), "instructor@example.com").enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().size();
                    if (lastKnownMessageCount != -1 && count > lastKnownMessageCount) {
                        Message latest = response.body().get(count - 1);
                        if (!latest.getSenderEmail().equals(sessionManager.getEmail())) {
                            NotificationHelper.showNotification(MainActivity.this, 
                                "New Message", 
                                latest.getContent());
                        }
                    }
                    lastKnownMessageCount = count;
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable t) {}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (monitorTimer != null) {
            monitorTimer.cancel();
        }
    }
}