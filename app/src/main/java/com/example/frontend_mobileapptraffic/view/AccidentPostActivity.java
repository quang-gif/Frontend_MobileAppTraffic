package com.example.frontend_mobileapptraffic.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.frontend_mobileapptraffic.Adapter.AccidentPostAdapter;
import com.example.frontend_mobileapptraffic.Presenter.AccidentPostPresenter;
import com.example.frontend_mobileapptraffic.R;
import com.example.frontend_mobileapptraffic.model.AccidentPost;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class AccidentPostActivity extends AppCompatActivity implements AccidentPostPresenter.AccidentPostView {
    private RecyclerView recyclerView;
    private AccidentPostAdapter adapter;
    private List<AccidentPost> postList = new ArrayList<>();
    private AccidentPostPresenter presenter;
    private MaterialToolbar topAppBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident_post);

        // Init Presenter
        presenter = new AccidentPostPresenter(this, this);

        // SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout_ap);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            presenter.loadPosts(); // Vuốt để reload
        });

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPosts_ap);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AccidentPostAdapter(this, postList, presenter);
        recyclerView.setAdapter(adapter);

        // Toolbar
        topAppBar = findViewById(R.id.toolbar_ap);
        topAppBar.setNavigationOnClickListener(v -> finish());

        // FloatingActionButton → mở CreatePostActivity
        findViewById(R.id.fabAddPost_ap).setOnClickListener(v -> {
            Intent intent = new Intent(AccidentPostActivity.this, CreatePostActivity.class);
            startActivity(intent);
        });

        // Load bài viết lần đầu
        presenter.loadPosts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mỗi lần quay lại màn hình sẽ reload post mới
        presenter.loadPosts();
    }

    @Override
    public void onPostsLoaded(List<AccidentPost> posts) {
        postList.clear();
        postList.addAll(posts);
        adapter.notifyDataSetChanged();

        // Tắt vòng xoay loading nếu đang hiển thị
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onPostsLoadError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();

        // Tắt vòng xoay loading khi lỗi
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onPostLiked(int position) {
        // Sau khi backend xử lý xong → refresh lại danh sách
        presenter.loadPosts();
    }

    @Override
    public void onPostReported(int position) {
        Toast.makeText(this, "Đã báo cáo bài viết " + position, Toast.LENGTH_SHORT).show();
        presenter.loadPosts();
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
