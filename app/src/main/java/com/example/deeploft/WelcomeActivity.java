package com.example.deeploft;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnStudent = findViewById(R.id.btn_join_student);
        Button btnInstructor = findViewById(R.id.btn_join_instructor);
        TextView tvLogin = findViewById(R.id.tv_already_have_account);

        btnStudent.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("PRE_SELECTED_ROLE", "STUDENT");
            startActivity(intent);
        });

        btnInstructor.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("PRE_SELECTED_ROLE", "INSTRUCTOR");
            startActivity(intent);
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}