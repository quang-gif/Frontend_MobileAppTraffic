package com.example.frontend_mobileapptraffic.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.frontend_mobileapptraffic.Adapter.FullscreenImageAdapter;
import com.example.frontend_mobileapptraffic.R;

import java.util.ArrayList;
import java.util.List;

public class FullscreenImageActivity extends AppCompatActivity {

    private ViewPager2 viewPagerImages;
    private ImageButton btnClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        viewPagerImages = findViewById(R.id.viewPagerImages);
        btnClose = findViewById(R.id.btnClose);

        // Nhận dữ liệu từ intent
        Intent intent = getIntent();
        List<String> imageUrls = intent.getStringArrayListExtra("images");
        int position = intent.getIntExtra("position", 0);

        // Setup adapter
        FullscreenImageAdapter adapter = new FullscreenImageAdapter(this, imageUrls);
        viewPagerImages.setAdapter(adapter);
        viewPagerImages.setCurrentItem(position, false);

        // Đóng Activity khi bấm nút X
        btnClose.setOnClickListener(v -> finish());
    }
}

