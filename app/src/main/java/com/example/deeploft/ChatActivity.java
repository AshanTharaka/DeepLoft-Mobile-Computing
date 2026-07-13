package com.example.deeploft;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.adapters.MessageAdapter;
import com.example.deeploft.models.Message;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private final List<Message> messageList = new ArrayList<>();
    private MessageAdapter adapter;
    private RecyclerView rvMessages;
    private EditText etMessage;
    private String receiverEmail;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        receiverEmail = getIntent().getStringExtra("RECEIVER_EMAIL");
        String receiverName = getIntent().getStringExtra("RECEIVER_NAME");
        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (receiverName != null) getSupportActionBar().setTitle(receiverName);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvMessages = findViewById(R.id.rv_chat_messages);
        etMessage = findViewById(R.id.et_chat_message);
        View btnSend = findViewById(R.id.btn_chat_send);

        adapter = new MessageAdapter(messageList, sessionManager.getEmail());
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        fetchMessages();
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void fetchMessages() {
        RetrofitClient.getApiService().getChat(sessionManager.getEmail(), receiverEmail).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageList.clear();
                    messageList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    rvMessages.scrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable t) {
                // Handle failure
            }
        });
    }

    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        if (content.isEmpty()) return;

        Message message = new Message(sessionManager.getEmail(), receiverEmail, content);
        RetrofitClient.getApiService().sendMessage(message).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    etMessage.setText("");
                    fetchMessages();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Toast.makeText(ChatActivity.this, "Failed to send", Toast.LENGTH_SHORT).show();
            }
        });
    }
}