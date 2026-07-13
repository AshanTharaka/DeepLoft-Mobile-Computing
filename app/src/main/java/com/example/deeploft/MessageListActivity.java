package com.example.deeploft;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.adapters.MessageListAdapter;
import com.example.deeploft.models.Message;
import com.example.deeploft.network.RetrofitClient;
import com.example.deeploft.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageListActivity extends AppCompatActivity {
    private final List<Message> chatList = new ArrayList<>();
    private MessageListAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        sessionManager = new SessionManager(this);
        
        Toolbar toolbar = findViewById(R.id.toolbar_msg_list);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.my_conversations_title);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rv_message_list);
        adapter = new MessageListAdapter(chatList, sessionManager.getEmail(), otherUserEmail -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("RECEIVER_EMAIL", otherUserEmail);
            intent.putExtra("RECEIVER_NAME", otherUserEmail);
            startActivity(intent);
        });
        
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        
        // Add divider
        androidx.recyclerview.widget.DividerItemDecoration divider = new androidx.recyclerview.widget.DividerItemDecoration(this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL);
        rv.addItemDecoration(divider);

        fetchConversations();
    }

    private void fetchConversations() {
        RetrofitClient.getApiService().getConversations(sessionManager.getEmail()).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chatList.clear();
                    chatList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable t) {
                Toast.makeText(MessageListActivity.this, "Error loading messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
}