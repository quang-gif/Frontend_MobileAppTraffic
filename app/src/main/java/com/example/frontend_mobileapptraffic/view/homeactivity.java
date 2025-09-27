package com.example.frontend_mobileapptraffic.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.frontend_mobileapptraffic.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class homeactivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        initViews();
        setupToolbar();
        setupNavigationDrawer();
        setupClickListeners();
        updateUserInfo();
        handleBackPress();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        // Xử lý click icon menu (3 gạch)
        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            if (itemId == R.id.nav_profile) {
                openProfile();
            } else if (itemId == R.id.nav_accident) {
                openAccidentBlackspots();
            } else if (itemId == R.id.nav_traffic) {
                openTrafficStatus();
            } else if (itemId == R.id.nav_fines) {
                openTrafficFines();
            } else if (itemId == R.id.nav_chatbot) {
                openChatbot();
            } else if (itemId == R.id.nav_settings) {
                openSettings();
            } else if (itemId == R.id.nav_help) {
                openHelp();
            } else if (itemId == R.id.nav_logout) {
                handleLogout();
            }
            return true;
        });
    }

    private void setupClickListeners() {
        if (findViewById(R.id.cardAccidentBlackspots) != null) {
            findViewById(R.id.cardAccidentBlackspots).setOnClickListener(v -> openAccidentBlackspots());
        }
        if (findViewById(R.id.cardTrafficStatus) != null) {
            findViewById(R.id.cardTrafficStatus).setOnClickListener(v -> openTrafficStatus());
        }
        if (findViewById(R.id.cardTrafficFines) != null) {
            findViewById(R.id.cardTrafficFines).setOnClickListener(v -> openTrafficFines());
        }
        if (findViewById(R.id.cardChatbot) != null) {
            findViewById(R.id.cardChatbot).setOnClickListener(v -> openChatbot());
        }
    }

    private void updateUserInfo() {
        TextView tvUserName = findViewById(R.id.userName);
        if (tvUserName != null) {
            String userName = "Nguyễn Văn A"; // Tạm thời
            tvUserName.setText(userName);
        }
    }

    // Các phương thức mở chức năng
    private void openProfile() {
        Toast.makeText(this, "Mở thông tin cá nhân", Toast.LENGTH_SHORT).show();
    }

    private void openAccidentBlackspots() {
        Toast.makeText(this, "Mở điểm đen tai nạn", Toast.LENGTH_SHORT).show();
    }

    private void openTrafficStatus() {
        Intent intent = new Intent(this, TrafficPostActivity.class);
        startActivity(intent);
    }

    private void openTrafficFines() {
        Toast.makeText(this, "Mở tra cứu phạt nguội", Toast.LENGTH_SHORT).show();
    }

    private void openChatbot() {
        Intent intent = new Intent(this, chatbotactivity.class);
        startActivity(intent);
    }

    private void openSettings() {
        Toast.makeText(this, "Mở cài đặt", Toast.LENGTH_SHORT).show();
    }

    private void openHelp() {
        Toast.makeText(this, "Mở trợ giúp", Toast.LENGTH_SHORT).show();
    }

    private void handleLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performLogout() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        preferences.edit().clear().apply();

        Intent intent = new Intent(this, loginactivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    homeactivity.super.onBackPressed();
                }
            }
        });
    }
}
