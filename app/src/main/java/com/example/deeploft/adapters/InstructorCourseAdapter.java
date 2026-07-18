package com.example.deeploft.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.deeploft.R;
import com.example.deeploft.models.Course;
import com.example.deeploft.network.RetrofitClient;
import java.util.List;

public class InstructorCourseAdapter extends RecyclerView.Adapter<InstructorCourseAdapter.ViewHolder> {

    private final List<Course> courses;
    private final OnCourseActionListener listener;

    public interface OnCourseActionListener {
        void onEdit(Course course);
        void onPreview(Course course);
    }

    public InstructorCourseAdapter(List<Course> courses, OnCourseActionListener listener) {
        this.courses = courses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_instructor_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.tvTitle.setText(course.getTitle());
        holder.tvPrice.setText("$" + course.getPrice());
        holder.tvStatus.setText(course.getStatus());

        if (holder.tvInstructor != null) {
            holder.tvInstructor.setText("by " + course.getInstructor());
        }

        if (course.getStatus() != null) {
            int color;
            switch (course.getStatus()) {
                case "PUBLISHED": color = android.R.color.holo_green_dark; break;
                case "PENDING_APPROVAL": color = android.R.color.holo_orange_dark; break;
                case "REJECTED": color = android.R.color.holo_red_dark; break;
                default: color = android.R.color.darker_gray; break;
            }
            holder.tvStatus.setBackgroundColor(holder.itemView.getContext().getResources().getColor(color));
        }

        if (course.getImageUrl() != null && !course.getImageUrl().isEmpty()) {
            String imageUrl = course.getImageUrl();
            if (!imageUrl.startsWith("http")) {
                imageUrl = RetrofitClient.getServiceUrl() + "uploads/images/" + imageUrl;
            }
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivImage);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(course));
        holder.btnPreview.setOnClickListener(v -> listener.onPreview(course));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvPrice, tvStatus, tvInstructor;
        Button btnEdit, btnPreview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_course_image);
            tvTitle = itemView.findViewById(R.id.tv_course_title);
            tvPrice = itemView.findViewById(R.id.tv_course_price);
            tvStatus = itemView.findViewById(R.id.tv_course_status);
            tvInstructor = itemView.findViewById(R.id.tv_course_instructor);
            btnEdit = itemView.findViewById(R.id.btn_edit_course);
            btnPreview = itemView.findViewById(R.id.btn_preview_course);
        }
    }
}