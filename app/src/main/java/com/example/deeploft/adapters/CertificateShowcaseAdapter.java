package com.example.deeploft.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deeploft.R;
import com.example.deeploft.models.Certificate;
import java.util.List;

public class CertificateShowcaseAdapter extends RecyclerView.Adapter<CertificateShowcaseAdapter.ViewHolder> {

    private final List<Certificate> certificates;

    public CertificateShowcaseAdapter(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_certificate_showcase, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Certificate cert = certificates.get(position);
        holder.tvCourse.setText(cert.getCourseTitle());
        holder.tvDate.setText("Issued on: " + cert.getIssuedDate());
    }

    @Override
    public int getItemCount() {
        return certificates.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourse, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourse = itemView.findViewById(R.id.tv_showcase_course);
            tvDate = itemView.findViewById(R.id.tv_showcase_date);
        }
    }
}