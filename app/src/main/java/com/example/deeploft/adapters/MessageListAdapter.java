package com.example.deeploft.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.R;
import com.example.deeploft.models.Message;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ListViewHolder> {

    private final List<Message> chats;
    private final OnChatClickListener listener;
    private final String currentUserEmail;

    public interface OnChatClickListener {
        void onChatClick(String otherUserEmail);
    }

    public MessageListAdapter(List<Message> chats, String currentUserEmail, OnChatClickListener listener) {
        this.chats = chats;
        this.currentUserEmail = currentUserEmail;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Message chat = chats.get(position);
        String otherUser = chat.getSenderEmail().equals(currentUserEmail) ? chat.getReceiverEmail() : chat.getSenderEmail();
        holder.tvUser.setText(otherUser);
        holder.tvLastMsg.setText(chat.getContent());
        holder.itemView.setOnClickListener(v -> listener.onChatClick(otherUser));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser, tvLastMsg;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(android.R.id.text1);
            tvLastMsg = itemView.findViewById(android.R.id.text2);
        }
    }
}