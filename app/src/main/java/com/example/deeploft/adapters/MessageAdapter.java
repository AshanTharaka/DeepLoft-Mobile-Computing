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
import com.example.deeploft.models.Message;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messages;
    private final String currentUserEmail;

    public MessageAdapter(List<Message> messages, String currentUserEmail) {
        this.messages = messages;
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.tvMessage.setText(message.getContent());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.tvMessage.getLayoutParams();
        if (message.getSenderEmail().equals(currentUserEmail)) {
            params.gravity = Gravity.END;
            holder.tvMessage.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame);
        } else {
            params.gravity = Gravity.START;
            holder.tvMessage.setBackgroundResource(android.R.drawable.editbox_dropdown_dark_frame);
        }
        holder.tvMessage.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_chat_message);
        }
    }
}