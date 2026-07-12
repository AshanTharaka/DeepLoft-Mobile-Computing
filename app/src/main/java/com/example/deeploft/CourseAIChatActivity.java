package com.example.deeploft;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.adapters.ChatAdapter;
import com.example.deeploft.models.ChatMessage;
import com.example.deeploft.models.Course;
import java.util.ArrayList;
import java.util.List;

public class CourseAIChatActivity extends AppCompatActivity {
    private final List<ChatMessage> messages = new ArrayList<>();
    private ChatAdapter adapter;
    private RecyclerView rvChat;
    private EditText etMessage;
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_ai_chat);

        course = (Course) getIntent().getSerializableExtra("COURSE_OBJECT");
        
        Toolbar toolbar = findViewById(R.id.toolbar_ai);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (course != null) {
                getSupportActionBar().setSubtitle("Course: " + course.getTitle());
            }
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvChat = findViewById(R.id.rv_ai_chat);
        etMessage = findViewById(R.id.et_ai_message);
        ImageButton btnSend = findViewById(R.id.btn_ai_send);

        adapter = new ChatAdapter(messages);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        // Welcome message
        messages.add(new ChatMessage("Hi! I'm your academic assistant for '" + 
            (course != null ? course.getTitle() : "this course") + 
            "'. How can I help you today?", false));
        adapter.notifyItemInserted(0);

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (!text.isEmpty()) {
            messages.add(new ChatMessage(text, true));
            adapter.notifyItemInserted(messages.size() - 1);
            rvChat.scrollToPosition(messages.size() - 1);
            etMessage.setText("");

            generateAIResponse(text);
        }
    }

    private void generateAIResponse(String userQuery) {
        rvChat.postDelayed(() -> {
            String responseText = getAcademicResponse(userQuery);
            messages.add(new ChatMessage(responseText, false));
            adapter.notifyItemInserted(messages.size() - 1);
            rvChat.scrollToPosition(messages.size() - 1);
        }, 1000);
    }

    private String getAcademicResponse(String query) {
        String lowerQuery = query.toLowerCase();
        if (course != null && lowerQuery.contains("about")) {
            return "This course, " + course.getTitle() + ", is taught by " + course.getInstructor() + 
                   ". It covers: " + course.getDescription();
        } else if (lowerQuery.contains("exam") || lowerQuery.contains("test")) {
            return "For this course, I recommend focusing on the key concepts mentioned in the introduction lesson. Would you like a summary of Lesson 1?";
        } else if (lowerQuery.contains("summary") || lowerQuery.contains("summarize")) {
            return "In this course, we learn the fundamentals of " + 
                   (course != null ? course.getCategory() : "the subject") + 
                   ". It's designed to help you master practical skills quickly.";
        } else {
            return "That's a great question! As your academic AI, I suggest reviewing the video for more details. Is there anything specific from the content you'd like me to explain?";
        }
    }
}