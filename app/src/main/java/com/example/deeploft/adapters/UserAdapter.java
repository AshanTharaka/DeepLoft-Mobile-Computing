package com.example.deeploft.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.R;
import com.example.deeploft.models.User;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> users;
    private final OnUserDeleteListener deleteListener;

    public interface OnUserDeleteListener {
        void onDelete(User user);
    }

    public UserAdapter(List<User> users, OnUserDeleteListener deleteListener) {
        this.users = users;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_management, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;
        Button btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvEmail = itemView.findViewById(R.id.tv_user_email);
            btnDelete = itemView.findViewById(R.id.btn_delete_user);
        }
    }
}