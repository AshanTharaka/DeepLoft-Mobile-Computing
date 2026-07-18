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
import com.example.deeploft.models.Enrollment;
import com.example.deeploft.network.RetrofitClient;
import java.util.List;

public class StudentCourseAdapter extends RecyclerView.Adapter<StudentCourseAdapter.ViewHolder> {

    private final List<Enrollment> enrollments;
    private final OnCourseActionListener listener;

    public interface OnCourseActionListener {
        void onContinue(Enrollment enrollment);
    }

    public StudentCourseAdapter(List<Enrollment> enrollments, OnCourseActionListener listener) {
        this.enrollments = enrollments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Enrollment enrollment = enrollments.get(position);
        holder.tvTitle.setText(enrollment.getCourse().getTitle());
        holder.tvInstructor.setText("by " + enrollment.getCourse().getInstructor());

        if (enrollment.getCourse().getImageUrl() != null && !enrollment.getCourse().getImageUrl().isEmpty()) {
            String imageUrl = enrollment.getCourse().getImageUrl();
            if (!imageUrl.startsWith("http")) {
                imageUrl = RetrofitClient.getServiceUrl() + "uploads/images/" + imageUrl;
            }
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivImage);
        }

        holder.btnContinue.setOnClickListener(v -> listener.onContinue(enrollment));
    }

    @Override
    public int getItemCount() {
        return enrollments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvInstructor;
        Button btnContinue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_course_image);
            tvTitle = itemView.findViewById(R.id.tv_course_title);
            tvInstructor = itemView.findViewById(R.id.tv_course_instructor);
            btnContinue = itemView.findViewById(R.id.btn_continue_watching);
        }
    }
}