package com.example.deeploft;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deeploft.models.User;
import com.example.deeploft.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private RadioGroup rgRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.et_reg_name);
        etEmail = findViewById(R.id.et_reg_email);
        etPassword = findViewById(R.id.et_reg_password);
        rgRole = findViewById(R.id.rg_role);
        Button btnRegister = findViewById(R.id.btn_register);
        TextView tvGoToLogin = findViewById(R.id.tv_go_to_login);

        String preSelectedRole = getIntent().getStringExtra("PRE_SELECTED_ROLE");
        if ("INSTRUCTOR".equals(preSelectedRole)) {
            rgRole.check(R.id.rb_instructor);
        } else {
            rgRole.check(R.id.rb_student);
        }

        btnRegister.setOnClickListener(v -> registerUser());
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        int selectedRoleId = rgRole.getCheckedRadioButtonId();
        String role = (selectedRoleId == R.id.rb_instructor) ? "INSTRUCTOR" : "STUDENT";

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User(name, email, password, role);
        RetrofitClient.getApiService().register(newUser).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registration successful. Please login.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    if (response.code() == 400) {
                        Toast.makeText(RegisterActivity.this, "Email already in use. Please use a different email.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}