package com.example.frontend_mobileapptraffic.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend_mobileapptraffic.Adapter.TrafficPostAdapter;
import com.example.frontend_mobileapptraffic.Presenter.TrafficPostPresenter;
import com.example.frontend_mobileapptraffic.R;
import com.example.frontend_mobileapptraffic.model.TrafficPost;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class TrafficPostActivity extends AppCompatActivity implements TrafficPostPresenter.TrafficPostView {

    private RecyclerView recyclerView;
    private TrafficPostAdapter adapter;
    private final List<TrafficPost> postList = new ArrayList<>();
    private TrafficPostPresenter presenter;
    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitytrafficpost);

        // Khởi tạo Presenter
        presenter = new TrafficPostPresenter(this, this);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrafficPostAdapter(this, postList, presenter);
        recyclerView.setAdapter(adapter);

        // Toolbar
        topAppBar = findViewById(R.id.toolbar);
        topAppBar.setNavigationOnClickListener(v -> finish());

        // FloatingActionButton → mở màn hình đăng bài
        findViewById(R.id.fabAddPost).setOnClickListener(v -> {
            Intent intent = new Intent(TrafficPostActivity.this, CreatePostActivity.class);
            startActivity(intent);
        });

        // Gọi API load danh sách bài viết
        presenter.loadPosts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mỗi khi quay lại màn hình thì reload lại danh sách
        presenter.loadPosts();
    }

    // -------------------- TrafficPostView implement --------------------

    @Override
    public void onPostsLoaded(List<TrafficPost> posts) {
        postList.clear();
        postList.addAll(posts);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPostsLoadError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostLiked(int position) {
        presenter.loadPosts(); // refresh like count
    }

    @Override
    public void onPostReported(int position) {
        Toast.makeText(this, "Đã báo cáo bài viết #" + position, Toast.LENGTH_SHORT).show();
        presenter.loadPosts(); // reload sau khi report
    }

    @Override
    public void onPostDeleted(int position) {
        if (position >= 0 && position < postList.size()) {
            postList.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(this, "Đã xóa bài viết", Toast.LENGTH_SHORT).show();
        }
    }
}
