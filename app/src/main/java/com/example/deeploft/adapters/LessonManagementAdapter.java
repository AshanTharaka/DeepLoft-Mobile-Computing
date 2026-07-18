package com.example.deeploft.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.R;
import com.example.deeploft.models.Lesson;
import java.util.Collections;
import java.util.List;

public class LessonManagementAdapter extends RecyclerView.Adapter<LessonManagementAdapter.ViewHolder> {

    private final List<Lesson> lessons;
    private final OnLessonActionListener listener;

    public interface OnLessonActionListener {
        void onEdit(Lesson lesson, int position);
        void onDelete(int position);
        void onReorder();
    }

    public LessonManagementAdapter(List<Lesson> lessons, OnLessonActionListener listener) {
        this.lessons = lessons;
        this.listener = listener;
    }

    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(lessons, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(lessons, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        listener.onReorder();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson_management, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.tvTitle.setText(lesson.getTitle());
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(lesson, position));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(position));
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_manage_lesson_title);
            btnEdit = itemView.findViewById(R.id.btn_edit_lesson);
            btnDelete = itemView.findViewById(R.id.btn_delete_lesson);
        }
    }
}