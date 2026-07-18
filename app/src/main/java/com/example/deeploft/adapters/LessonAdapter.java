package com.example.deeploft.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.R;
import com.example.deeploft.models.Lesson;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private final List<Lesson> lessons;
    private final List<Long> completedLessonIds;
    private final OnLessonClickListener listener;

    public interface OnLessonClickListener {
        void onLessonClick(Lesson lesson);
    }

    public LessonAdapter(List<Lesson> lessons, List<Long> completedLessonIds, OnLessonClickListener listener) {
        this.lessons = lessons;
        this.completedLessonIds = completedLessonIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.tvTitle.setText(lesson.getTitle());
        
        int mins = lesson.getDurationMinutes();
        if (mins > 0) {
            holder.tvDuration.setText(mins + " mins");
        } else {
            holder.tvDuration.setText("Short Video");
        }
        
        if (completedLessonIds.contains(lesson.getId())) {
            holder.ivStatus.setImageResource(android.R.drawable.checkbox_on_background);
        } else {
            holder.ivStatus.setImageResource(android.R.drawable.ic_media_play);
        }

        holder.itemView.setOnClickListener(v -> listener.onLessonClick(lesson));
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDuration;
        ImageView ivStatus;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_lesson_title);
            tvDuration = itemView.findViewById(R.id.tv_lesson_duration);
            ivStatus = itemView.findViewById(R.id.iv_lesson_status);
        }
    }
}