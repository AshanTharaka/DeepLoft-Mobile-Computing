package com.example.deeploft.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.R;
import com.example.deeploft.models.ChatMessage;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.tvMessage.setText(message.getText());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.tvMessage.getLayoutParams();
        if (message.isUser()) {
            params.gravity = Gravity.END;
            params.setMargins(50, 4, 0, 4);
            holder.tvMessage.setBackgroundResource(R.drawable.bg_chat_bubble_user);
        } else {
            params.gravity = Gravity.START;
            params.setMargins(0, 4, 50, 4);
            holder.tvMessage.setBackgroundResource(R.drawable.bg_chat_bubble_ai);
        }
        holder.tvMessage.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_chat_message);
        }
    }
}