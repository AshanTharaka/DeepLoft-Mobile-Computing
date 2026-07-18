package com.example.deeploft.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.deeploft.CourseDetailActivity;
import com.example.deeploft.R;
import com.example.deeploft.models.Course;
import com.example.deeploft.network.RetrofitClient;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private final List<Course> courses;
    private final OnCourseClickListener listener;
    private final boolean isSmallLayout;

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public CourseAdapter(List<Course> courses) {
        this(courses, false, null);
    }

    public CourseAdapter(List<Course> courses, OnCourseClickListener listener) {
        this(courses, false, listener);
    }

    public CourseAdapter(List<Course> courses, boolean isSmallLayout, OnCourseClickListener listener) {
        this.courses = courses;
        this.isSmallLayout = isSmallLayout;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isSmallLayout ? R.layout.item_course_small : R.layout.item_course;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.tvTitle.setText(course.getTitle());
        holder.tvPrice.setText("$" + course.getPrice());

        if (holder.tvInstructor != null) {
            holder.tvInstructor.setText("by " + course.getInstructor());
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
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCourseClick(course);
            } else {
                Intent intent = new Intent(v.getContext(), CourseDetailActivity.class);
                intent.putExtra("COURSE_OBJECT", course);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvInstructor, tvPrice;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_course_image);
            tvTitle = itemView.findViewById(R.id.tv_course_title);
            tvInstructor = itemView.findViewById(R.id.tv_course_instructor);
            tvPrice = itemView.findViewById(R.id.tv_course_price);
        }
    }
}