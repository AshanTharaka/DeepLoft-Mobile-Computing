package com.example.deeploft;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavGraph;
import androidx.navigation.ui.NavigationUI;

import com.example.deeploft.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        // Force white background for the window to prevent black screens on buggy emulators
        getWindow().getDecorView().setBackgroundColor(android.graphics.Color.WHITE);
        
        setContentView(R.layout.activity_main);
        
        requestNotificationPermission();
        
        // Tiny delay to ensure NavHostFragment is fully attached before setting the graph
        new Handler(Looper.getMainLooper()).postDelayed(this::setupNavigation, 50);
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

            // Clear and set role-specific menu
            bottomNav.getMenu().clear();
            if (email != null && email.equalsIgnoreCase("ashan@deeploft.com")) {
                bottomNav.inflateMenu(R.menu.bottom_nav_admin);
            } else if (role != null && role.equalsIgnoreCase("INSTRUCTOR")) {
                bottomNav.inflateMenu(R.menu.bottom_nav_instructor);
            } else {
                bottomNav.inflateMenu(R.menu.bottom_nav_student);
            }

            // Dynamically adjust start destination
            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
            if ((email != null && email.equalsIgnoreCase("ashan@deeploft.com")) || (role != null && role.equalsIgnoreCase("INSTRUCTOR"))) {
                navGraph.setStartDestination(R.id.navigation_my_courses); 
            } else {
                navGraph.setStartDestination(R.id.navigation_home); 
            }
            navController.setGraph(navGraph);

            NavigationUI.setupWithNavController(bottomNav, navController);
            
            // Ensure UI is drawn
            findViewById(R.id.main).invalidate();
        }
    }
}
