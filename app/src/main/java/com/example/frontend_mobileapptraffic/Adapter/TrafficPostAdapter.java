package com.example.frontend_mobileapptraffic.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frontend_mobileapptraffic.Presenter.TrafficPostPresenter;
import com.example.frontend_mobileapptraffic.R;
import com.example.frontend_mobileapptraffic.model.TrafficPost;
import com.example.frontend_mobileapptraffic.view.FullscreenImageActivity;

import java.util.ArrayList;
import java.util.List;

public class TrafficPostAdapter extends RecyclerView.Adapter<TrafficPostAdapter.PostViewHolder> {

    private final Context context;
    private final List<TrafficPost> postList;
    private final TrafficPostPresenter presenter;

    public TrafficPostAdapter(Context context, List<TrafficPost> postList, TrafficPostPresenter presenter) {
        this.context = context;
        this.postList = postList;
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.temtrafficpost, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        TrafficPost post = postList.get(position);

        // Username
        holder.btnUsername.setText(post.getUsername());

        // Time
        if (post.getTimestamp() != null && !post.getTimestamp().isEmpty()) {
            holder.tvTime.setText(formatTimestamp(post.getTimestamp()));
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

        // Update trạng thái Like
        updateLikeUI(holder.btnLike, post);

        // Sự kiện Like
        holder.btnLike.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_POSITION) return;

            presenter.toggleLike(post.getIdPost(), adapterPos);

            // Optimistic update (cập nhật ngay trên UI)
            if (post.isLikedByUser()) {
                post.setLikedByUser(false);
                post.setLikeTotal(Math.max(0, post.getLikeTotal() - 1));
            } else {
                post.setLikedByUser(true);
                post.setLikeTotal(post.getLikeTotal() + 1);
            }
            notifyItemChanged(adapterPos);
        });
    }

    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    // ----------------- ViewHolder -----------------
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        Button btnUsername, btnReport, btnLike;
        TextView tvTime, tvLocation, tvContent;
        RecyclerView recyclerViewImages;
        ImageButton btnMore;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            btnUsername = itemView.findViewById(R.id.btnUsername);
            btnReport   = itemView.findViewById(R.id.btnReport);
            btnLike     = itemView.findViewById(R.id.btnLike);
            tvTime      = itemView.findViewById(R.id.tvTime);
            tvLocation  = itemView.findViewById(R.id.tvLocation);
            tvContent   = itemView.findViewById(R.id.tvContent);
            recyclerViewImages = itemView.findViewById(R.id.recyclerViewImages);
            btnMore     = itemView.findViewById(R.id.btnMore);
        }
    }

    // ----------------- Image Adapter -----------------
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
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
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

    // ----------------- Helper -----------------
    private void updateLikeUI(Button btnLike, TrafficPost post) {
        btnLike.setText("👍 " + post.getLikeTotal());
        int color = post.isLikedByUser()
                ? ContextCompat.getColor(context, R.color.primary_color)
                : ContextCompat.getColor(context, R.color.text_secondary);
        btnLike.setTextColor(color);
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
}
