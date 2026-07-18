package com.example.deeploft.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.deeploft.R;
import com.example.deeploft.utils.SessionManager;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_container, container, false);
        
        SessionManager sessionManager = new SessionManager(requireContext());
        String role = sessionManager.getRole();
        String email = sessionManager.getEmail();

        Fragment dashboardToShow;

        if (email.equalsIgnoreCase("ashan@deeploft.com")) {
            dashboardToShow = new AdminDashboardFragment();
        } else if (role.equalsIgnoreCase("INSTRUCTOR")) {
            dashboardToShow = new InstructorDashboardFragment();
        } else {
            dashboardToShow = new StudentDashboardFragment();
        }

        getChildFragmentManager().beginTransaction()
                .replace(R.id.dashboard_content_container, dashboardToShow)
                .commit();

        return view;
    }
}