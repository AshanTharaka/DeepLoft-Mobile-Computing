package com.example.deeploft.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.deeploft.AchievementsActivity;
import com.example.deeploft.AdminDashboardActivity;
import com.example.deeploft.LoginActivity;
import com.example.deeploft.MessageListActivity;
import com.example.deeploft.R;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;
import com.google.android.material.switchmaterial.SwitchMaterial;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SessionManager sessionManager = new SessionManager(requireContext());
        
        TextView tvUsername = view.findViewById(R.id.tv_username);
        TextView tvEmail = view.findViewById(R.id.tv_email);
        SwitchMaterial swDarkMode = view.findViewById(R.id.sw_dark_mode);
        Button btnLogout = view.findViewById(R.id.btn_logout);
        Button btnDeleteAccount = view.findViewById(R.id.btn_delete_account);
        Button btnMessages = view.findViewById(R.id.btn_messages);
        Button btnAchievements = view.findViewById(R.id.btn_achievements);
        Button btnInstructor = view.findViewById(R.id.btn_instructor_dashboard);
        Button btnAdmin = view.findViewById(R.id.btn_admin_dashboard);

        String currentEmail = sessionManager.getEmail();
        String currentRole = sessionManager.getRole();
        boolean isAdmin = currentEmail.equalsIgnoreCase("ashan@deeploft.com");
        
        tvUsername.setText(sessionManager.getName() + " (" + currentRole + ")");
        tvEmail.setText(currentEmail);

        ImageView ivProfile = view.findViewById(R.id.iv_profile_pic);
        if (isAdmin) {
            ivProfile.setImageResource(R.drawable.ic_deeploft_logo);
        }

        // Achievements is only for students
        if (!isAdmin && currentRole.equalsIgnoreCase("STUDENT")) {
            btnAchievements.setVisibility(View.VISIBLE);
        } else {
            btnAchievements.setVisibility(View.GONE);
        }

        btnAchievements.setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), AchievementsActivity.class))
        );

        // Role-based visibility
        if (isAdmin) {
            btnAdmin.setVisibility(View.VISIBLE);
        } else {
            btnAdmin.setVisibility(View.GONE);
        }

        if (currentRole.equals("INSTRUCTOR")) {
            btnInstructor.setVisibility(View.VISIBLE);
        } else {
            btnInstructor.setVisibility(View.GONE);
        }

        swDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        btnAdmin.setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), AdminDashboardActivity.class))
        );

        btnInstructor.setOnClickListener(v -> 
            Navigation.findNavController(view).navigate(R.id.action_profile_to_instructor)
        );

        btnMessages.setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), MessageListActivity.class))
        );

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        btnDeleteAccount.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to permanently delete your account? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> deleteAccount(sessionManager))
                    .setNegativeButton("Cancel", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        return view;
    }

    private void deleteAccount(SessionManager sessionManager) {
        RetrofitClient.getApiService().deleteUser(sessionManager.getEmail()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_SHORT).show();
                    }
                    sessionManager.logout();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    requireActivity().finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Delete failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}