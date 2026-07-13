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
                // Ensure no double slashes
                String baseUrl = RetrofitClient.getServiceUrl();
                if (baseUrl.endsWith("/")) {
                    baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
                }
                imageUrl = baseUrl + "/uploads/images/" + imageUrl;
            }
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.bg_chat_bubble_ai)
                    .error(getPlaceholderForCategory(course.getCategory()))
                    .into(holder.ivImage);
        } else {
            // Provide a high-quality category-based placeholder
            Glide.with(holder.itemView.getContext())
                    .load(getPlaceholderForCategory(course.getCategory()))
                    .centerCrop()
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

    private String getPlaceholderForCategory(String category) {
        if (category == null) return "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=500";
        switch (category.toLowerCase()) {
            case "development": return "https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=500";
            case "design": return "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=500";
            case "ai": return "https://images.unsplash.com/photo-1677442136019-21780ecad995?w=500";
            case "science": return "https://images.unsplash.com/photo-1507413245164-6160d8298b31?w=500";
            case "business": return "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=500";
            case "music": return "https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=500";
            case "academic": return "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=500";
            default: return "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=500";
        }
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