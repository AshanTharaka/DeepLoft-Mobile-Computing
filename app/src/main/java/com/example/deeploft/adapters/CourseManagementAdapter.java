package com.example.deeploft.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.R;
import com.example.deeploft.models.Course;
import java.util.List;

public class CourseManagementAdapter extends RecyclerView.Adapter<CourseManagementAdapter.ViewHolder> {

    private final List<Course> courses;
    private final OnCourseActionListener listener;

    public interface OnCourseActionListener {
        void onDelete(Course course);
        void onPreview(Course course);
    }

    public CourseManagementAdapter(List<Course> courses, OnCourseActionListener listener) {
        this.courses = courses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_management, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.tvTitle.setText(course.getTitle());
        holder.tvInstructor.setText(course.getInstructor());
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(course));
        holder.btnPreview.setOnClickListener(v -> listener.onPreview(course));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvInstructor;
        Button btnDelete, btnPreview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_manage_course_title);
            tvInstructor = itemView.findViewById(R.id.tv_manage_course_instructor);
            btnDelete = itemView.findViewById(R.id.btn_delete_course_admin);
            btnPreview = itemView.findViewById(R.id.btn_preview_course_admin);
        }
    }
}