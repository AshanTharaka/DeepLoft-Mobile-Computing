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

public class CourseApprovalAdapter extends RecyclerView.Adapter<CourseApprovalAdapter.ViewHolder> {

    private final List<Course> courses;
    private final OnCourseApprovalListener listener;

    public interface OnCourseApprovalListener {
        void onApprove(Course course);
        void onReject(Course course);
        void onPreview(Course course);
    }

    public CourseApprovalAdapter(List<Course> courses, OnCourseApprovalListener listener) {
        this.courses = courses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_approval, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.tvTitle.setText(course.getTitle());
        holder.tvInstructor.setText("by " + course.getInstructor());

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(course));
        holder.btnReject.setOnClickListener(v -> listener.onReject(course));
        holder.btnPreview.setOnClickListener(v -> listener.onPreview(course));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvInstructor;
        Button btnApprove, btnReject, btnPreview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_approve_course_title);
            tvInstructor = itemView.findViewById(R.id.tv_approve_course_instructor);
            btnApprove = itemView.findViewById(R.id.btn_approve_action);
            btnReject = itemView.findViewById(R.id.btn_reject_action);
            btnPreview = itemView.findViewById(R.id.btn_approve_preview);
        }
    }
}