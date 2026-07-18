package com.example.deeploft.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.R;
import com.example.deeploft.models.StudentProgress;
import java.util.List;

public class StudentProgressAdapter extends RecyclerView.Adapter<StudentProgressAdapter.ViewHolder> {

    private final List<StudentProgress> progressList;

    public StudentProgressAdapter(List<StudentProgress> progressList) {
        this.progressList = progressList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_progress, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentProgress progress = progressList.get(position);
        holder.tvEmail.setText(progress.getStudentEmail());
        holder.tvCourse.setText("Course: " + progress.getCourseTitle());
        holder.pbCompletion.setProgress(progress.getCompletionPercentage());
        holder.tvPercentage.setText(progress.getCompletionPercentage() + "%");
    }

    @Override
    public int getItemCount() {
        return progressList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail, tvCourse, tvPercentage;
        ProgressBar pbCompletion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tv_progress_student_email);
            tvCourse = itemView.findViewById(R.id.tv_progress_course_title);
            tvPercentage = itemView.findViewById(R.id.tv_progress_percentage);
            pbCompletion = itemView.findViewById(R.id.pb_student_completion);
        }
    }
}