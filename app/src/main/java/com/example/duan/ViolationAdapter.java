package com.example.duan;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViolationAdapter extends RecyclerView.Adapter<ViolationAdapter.ViolationViewHolder> {

    private final List<Violation> violations;

    public ViolationAdapter(List<Violation> violations) {
        this.violations = violations;
    }

    @NonNull
    @Override
    public ViolationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_violation_card, parent, false);
        return new ViolationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViolationViewHolder holder, int position) {
        Violation v = violations.get(position);

        holder.tvTitle.setText( (position + 1));
        holder.tvLicensePlate.setText( safe(v.licensePlate));
        holder.tvPlateColor.setText( safe(v.plateColor));
        holder.tvVehicleType.setText( safe(v.vehicleType));
        holder.tvTime.setText( safe(v.violationTime));
        holder.tvLocation.setText( safe(v.violationLocation));
        holder.tvBehavior.setText( safe(v.violationBehavior));
        holder.tvStatus.setText(safe(v.status));
        holder.tvDetectedBy.setText(safe(v.detectedBy));

        // ✅ Đổi background theo status
        if (v.getStatus() != null && v.getStatus().equalsIgnoreCase("Chưa xử phạt")) {
            // Chip
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_unpaid);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.error_color));

            // Header
            holder.headerLayout.setBackgroundResource(R.drawable.bg_header_unpaid);// đỏ nhạt
        } else {
            // Chip
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_paid);
            holder.tvStatus.setTextColor(Color.parseColor("#228B22"));

            // Header
            holder.headerLayout.setBackgroundResource(R.drawable.bg_header_paid); // xanh nhạt
        }



        // Nơi giải quyết
        if (v.paymentPlaces != null && !v.paymentPlaces.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String place : v.paymentPlaces) {
                sb.append("• ").append(place).append("\n");
            }
            holder.tvPaymentPlaces.setText(sb.toString().trim());
        } else {
            holder.tvPaymentPlaces.setText("—");
        }

//        // Màu trạng thái
//        if (v.status != null && v.status.toLowerCase().contains("chưa")) {
//            holder.tvStatus.setTextColor(0xFFD32F2F); // đỏ
//        } else {
//            holder.tvStatus.setTextColor(0xFF2E7D32); // xanh
//        }
    }

    @Override
    public int getItemCount() {
        return violations.size();
    }

    static class ViolationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLicensePlate, tvPlateColor, tvVehicleType,
                tvTime, tvLocation, tvBehavior, tvStatus, tvDetectedBy, tvPaymentPlaces;
        LinearLayout headerLayout;

        public ViolationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle         = itemView.findViewById(R.id.tvTitle);
            tvLicensePlate  = itemView.findViewById(R.id.tvLicensePlate);
            tvPlateColor    = itemView.findViewById(R.id.tvPlateColor);
            tvVehicleType   = itemView.findViewById(R.id.tvVehicleType);
            tvTime          = itemView.findViewById(R.id.tvTime);
            tvLocation      = itemView.findViewById(R.id.tvLocation);
            tvBehavior      = itemView.findViewById(R.id.tvBehavior);
            tvStatus        = itemView.findViewById(R.id.tvStatus);
            tvDetectedBy    = itemView.findViewById(R.id.tvDetectedBy);
            tvPaymentPlaces = itemView.findViewById(R.id.tvPaymentPlaces);
            headerLayout = itemView.findViewById(R.id.headerLayout);

        }
    }

    private String safe(String s) {
        return (s == null || s.isEmpty()) ? "—" : s;
    }
}
