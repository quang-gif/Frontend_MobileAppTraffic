package com.example.frontend_mobileapptraffic.view;

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
    private List<TrafficPost> postList = new ArrayList<>();
    private TrafficPostPresenter presenter;
    private MaterialToolbar topAppBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitytrafficpost);

        recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrafficPostAdapter(this, postList);
        recyclerView.setAdapter(adapter);

        topAppBar = findViewById(R.id.toolbar);
        topAppBar.setNavigationOnClickListener(v -> finish());

        presenter = new TrafficPostPresenter(this, this);

        // gọi API lấy danh sách post
        presenter.loadPosts();
    }

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
        Toast.makeText(this, "Đã thích bài viết " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostReported(int position) {
        Toast.makeText(this, "Đã báo cáo bài viết " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostDeleted(int position) {
        postList.remove(position);
        adapter.notifyItemRemoved(position);
        Toast.makeText(this, "Đã xóa bài viết", Toast.LENGTH_SHORT).show();
    }
}
