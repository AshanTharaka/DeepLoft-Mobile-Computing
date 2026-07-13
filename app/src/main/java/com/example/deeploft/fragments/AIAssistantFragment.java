package com.example.deeploft.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deeploft.R;
import com.example.deeploft.adapters.ChatAdapter;
import com.example.deeploft.models.ChatMessage;
import com.example.deeploft.models.StudyPlan;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AIAssistantFragment extends Fragment {
    private final List<ChatMessage> messages = new ArrayList<>();
    private ChatAdapter adapter;
    private RecyclerView rvChat;
    private EditText etMessage;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_assistant, container, false);

        sessionManager = new SessionManager(requireContext());
        rvChat = view.findViewById(R.id.rv_chat);
        etMessage = view.findViewById(R.id.et_message);
        View btnSend = view.findViewById(R.id.btn_send);

        adapter = new ChatAdapter(messages);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(adapter);

        // Initial message if list is empty
        if (messages.isEmpty()) {
            String role = sessionManager.getRole();
            String welcomeMsg = role.equalsIgnoreCase("INSTRUCTOR") ? 
                "Hello Professor! I am your DeepLoft Instructor Assistant. How can I help you with your teaching today?" :
                "Hello! I am DeepLoft's Academic AI. How can I help you with your studies today?";
            messages.add(new ChatMessage(welcomeMsg, false));
            adapter.notifyItemInserted(0);
        }

        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (!text.isEmpty()) {
            messages.add(new ChatMessage(text, true));
            adapter.notifyItemInserted(messages.size() - 1);
            rvChat.scrollToPosition(messages.size() - 1);
            etMessage.setText("");

            // Add a "typing" effect
            messages.add(new ChatMessage("DeepLoft AI is thinking...", false));
            adapter.notifyItemInserted(messages.size() - 1);
            rvChat.scrollToPosition(messages.size() - 1);

            // Improved Mock AI response logic
            String responseText = getAiResponse(text);
            
            rvChat.postDelayed(() -> {
                // Remove typing indicator and add real response
                messages.remove(messages.size() - 1);
                messages.add(new ChatMessage(responseText, false));
                adapter.notifyDataSetChanged();
                rvChat.scrollToPosition(messages.size() - 1);
            }, 1500);
        }
    }

    private String getAiResponse(String input) {
        String lowerInput = input.toLowerCase();
        String role = sessionManager.getRole();
        
        if (role.equalsIgnoreCase("INSTRUCTOR")) {
            if (lowerInput.contains("hello") || lowerInput.contains("hi")) {
                return "Hello! I can help you structure your course, generate lesson summaries, or analyze your sales trends.";
            } else if (lowerInput.contains("structure") || lowerInput.contains("outline")) {
                return "To create a great course structure, I recommend starting with a strong 'Introduction' video. Would you like me to help you draft a specific lesson outline?";
            } else if (lowerInput.contains("money") || lowerInput.contains("earn") || lowerInput.contains("commission")) {
                return "On DeepLoft, you keep 80% of every sale. You can track your earnings in real-time on your Dashboard.";
            } else {
                return "As your teaching assistant, I'm here to help you create world-class academic content. Ask me about course planning!";
            }
        } else {
            if (lowerInput.contains("hello") || lowerInput.contains("hi")) {
                return "Hello! I am DeepLoft's Academic AI. I can help you summarize lessons, explain complex concepts, or recommend courses. What's on your mind?";
            } else if (lowerInput.contains("summarize") || lowerInput.contains("summary")) {
                return "To provide a summary, please tell me which course or lesson you are referring to. I can distill key points for you!";
            } else if (lowerInput.contains("explain")) {
                return "I'd be happy to explain! Is there a specific academic topic you're struggling with?";
            } else if (lowerInput.contains("course") || lowerInput.contains("recommend")) {
                return "I recommend checking out our new 'Astrophysics for Everyone' or 'Digital Marketing 101'. We also have 'Advanced Calculus' for university-level study!";
            } else if (lowerInput.contains("science") || lowerInput.contains("math")) {
                return "For Science, we have 'Astrophysics' and 'Modern Chemistry'. For Math, I highly recommend 'Advanced Calculus'.";
            } else if (lowerInput.contains("design") || lowerInput.contains("art")) {
                return "Our 'Graphic Design Masterclass' and 'UI/UX Design for Mobile' are perfect for creative minds!";
            } else if (lowerInput.contains("code") || lowerInput.contains("program")) {
                return "You should try 'Fullstack Web Bootcamp' or 'Android Development with Java'. They are very practical!";
            } else if (lowerInput.contains("plan") || lowerInput.contains("schedule")) {
                generateStudyPlanForUser();
                return "I'm generating a personalized study plan for your current courses right now! You can view it in the 'My AI Study Plan' section of any course.";
            } else {
                return "That's an interesting question. I'm currently in 'Academic Mode'. Try asking me to summarize a topic or explain a concept from your courses!";
            }
        }
    }

    private void generateStudyPlanForUser() {
        // Study plan logic remains the same
        StudyPlan request = new StudyPlan(sessionManager.getEmail(), "Android Development with Java", "Complete in 2 weeks");
        RetrofitClient.getApiService().generateStudyPlan(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<StudyPlan> call, @NonNull Response<StudyPlan> response) { }

            @Override
            public void onFailure(@NonNull Call<StudyPlan> call, @NonNull Throwable t) { }
        });
    }
}