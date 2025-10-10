package com.example.frontend_mobileapptraffic.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frontend_mobileapptraffic.Presenter.AccidentPostPresenter;
import com.example.frontend_mobileapptraffic.Presenter.AccidentReportPresenter;
import com.example.frontend_mobileapptraffic.R;
import com.example.frontend_mobileapptraffic.model.AccidentPost;
import com.example.frontend_mobileapptraffic.view.AccidentPostSuccessActivity;
import com.example.frontend_mobileapptraffic.view.AccidentReportSuccessActivity;
import com.example.frontend_mobileapptraffic.view.CreateAccidentPostActivity;
import com.example.frontend_mobileapptraffic.view.FullscreenImageActivity;

import java.util.ArrayList;
import java.util.List;

public class AccidentPostAdapter extends RecyclerView.Adapter<AccidentPostAdapter.PostViewHolder> {
    private final Context context;
    private final List<AccidentPost> postList;
    private final AccidentPostPresenter presenter;

    public AccidentPostAdapter(Context context, List<AccidentPost> postList, AccidentPostPresenter presenter) {
        this.context = context;
        this.postList = postList;
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.temaccidentpost, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        AccidentPost post = postList.get(position);

        // Username
        holder.btnUsername.setText(post.getUsername());

        // Time
        if (post.getCreatedAt() != null && !post.getCreatedAt().isEmpty()) {
            holder.tvTime.setText(formatTimestamp(post.getCreatedAt()));
        } else {
            holder.tvTime.setText("Không rõ thời gian");
        }

        // Location
        holder.tvLocation.setText(post.getLocation());

        // Content
        holder.tvContent.setText(post.getContent());

        // Images
        if (post.getImageUrls() != null && !post.getImageUrls().isEmpty()) {
            holder.recyclerViewImages.setVisibility(View.VISIBLE);
            ImageAdapter imageAdapter = new ImageAdapter(context, post.getImageUrls());
            holder.recyclerViewImages.setLayoutManager(
                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            );
            holder.recyclerViewImages.setAdapter(imageAdapter);
        } else {
            holder.recyclerViewImages.setVisibility(View.GONE);
        }

        // Like button
        updateLikeUI(holder.btnLike, post);
        holder.btnLike.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_POSITION) return;

            presenter.toggleLike(post.getIdAcPost(), adapterPos);

            // Optimistic update (UI cập nhật ngay)
            if (post.isLikedByUser()) {
                post.setLikedByUser(false);
                post.setLikeTotal(Math.max(0, post.getLikeTotal() - 1));
            } else {
                post.setLikedByUser(true);
                post.setLikeTotal(post.getLikeTotal() + 1);
            }
            notifyItemChanged(adapterPos);
        });

        // Report button
        holder.btnReport.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_POSITION) return;

            AccidentPost currentPost = postList.get(adapterPos);

            android.util.Log.e("AccidentPostAdapter", "=== DEBUG POST ===");
            android.util.Log.e("AccidentPostAdapter", "Position: " + adapterPos);
            android.util.Log.e("AccidentPostAdapter", "Post ID: " + currentPost.getIdAcPost());
            android.util.Log.e("AccidentPostAdapter", "Post ID type: " +
                    (currentPost.getIdAcPost() == null ? "NULL" : currentPost.getIdAcPost().getClass().getSimpleName()));
            android.util.Log.e("AccidentPostAdapter", "Content: " + currentPost.getContent());
            android.util.Log.e("AccidentPostAdapter", "Username: " + currentPost.getUsername());
            android.util.Log.e("AccidentPostAdapter", "==================");


            // Inflate layout report
            View dialogView = LayoutInflater.from(context).inflate(R.layout.spinner_report, null);

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create();

            // Ánh xạ view
            RadioGroup rgReasons = dialogView.findViewById(R.id.rgReasons);
            View layoutOtherReason = dialogView.findViewById(R.id.layoutOtherReason);
            TextView etOtherReason = dialogView.findViewById(R.id.etOtherReason);
            Button btnSubmit = dialogView.findViewById(R.id.btnSubmitReport);
            ImageButton btnClose = dialogView.findViewById(R.id.btnClose);

            // Xử lý show/hide khi chọn "Khác"
            rgReasons.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rbOther) {
                    layoutOtherReason.setVisibility(View.VISIBLE);
                } else {
                    layoutOtherReason.setVisibility(View.GONE);
                }
            });

            // Nút đóng
            btnClose.setOnClickListener(x -> dialog.dismiss());

            // Submit báo cáo
            btnSubmit.setOnClickListener(x -> {
                int selectedId = rgReasons.getCheckedRadioButtonId();
                String reason = "";

                if (selectedId == -1) {
                    Toast.makeText(context, "Vui lòng chọn lý do", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedId == R.id.rbOther) {
                    reason = etOtherReason.getText().toString().trim();
                    if (reason.isEmpty()) {
                        Toast.makeText(context, "Hãy nhập lý do chi tiết", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (selectedId == R.id.rbFalseInfo) {
                    reason = "Nội dung sai sự thật";
                } else if (selectedId == R.id.rbInappropriate) {
                    reason = "Ngôn ngữ không phù hợp";
                } else if (selectedId == R.id.rbSpam) {
                    reason = "Spam / Quảng cáo";
                }

                // Gọi API report
                AccidentReportPresenter reportPresenter = new AccidentReportPresenter(context);
                reportPresenter.reportPost(
                        currentPost.getIdAcPost(),
                        reason,
                        () -> {
                            long postId = currentPost.getIdAcPost();
                            dialog.dismiss();
                            Intent intent = new Intent(context, AccidentReportSuccessActivity.class);
                            context.startActivity(intent);
                        },
                        error -> Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                );
            });

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    private String formatTimestamp(String isoString) {
        try {
            String date = isoString.substring(0, 10);  // yyyy-MM-dd
            String time = isoString.substring(11, 16); // HH:mm
            String[] parts = date.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0] + " " + time;
        } catch (Exception e) {
            return isoString;
        }
    }

    private void updateLikeUI(Button btnLike, AccidentPost post) {
        btnLike.setText("👍 " + post.getLikeTotal());
        int color = post.isLikedByUser()
                ? ContextCompat.getColor(context, R.color.primary_color)
                : ContextCompat.getColor(context, R.color.text_secondary);
        btnLike.setTextColor(color);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        Button btnUsername, btnReport, btnLike;
        TextView tvTime, tvLocation, tvContent;
        RecyclerView recyclerViewImages;
        ImageButton btnMore;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            btnUsername = itemView.findViewById(R.id.btnUsername_ap);
            btnReport   = itemView.findViewById(R.id.btnReport_ap);
            btnLike     = itemView.findViewById(R.id.btnLike_ap);
            tvTime      = itemView.findViewById(R.id.tvTime_ap);
            tvLocation  = itemView.findViewById(R.id.tvLocation_ap);
            tvContent   = itemView.findViewById(R.id.tvContent_ap);
            recyclerViewImages = itemView.findViewById(R.id.recyclerViewImages_ap);
            btnMore     = itemView.findViewById(R.id.btnMore_ap);
        }
    }

    static class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
        private final Context context;
        private final List<String> imageUrls;

        ImageAdapter(Context context, List<String> imageUrls) {
            this.context = context;
            this.imageUrls = imageUrls;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
            String url = imageUrls.get(position) != null ? imageUrls.get(position) : "";
            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(holder.ivPostImage);

            holder.ivPostImage.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullscreenImageActivity.class);
                intent.putStringArrayListExtra("images", new ArrayList<>(imageUrls));
                intent.putExtra("position", position);
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return imageUrls != null ? imageUrls.size() : 0;
        }

        static class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView ivPostImage;
            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                ivPostImage = itemView.findViewById(R.id.ivPostImage);
            }
        }
    }
}
