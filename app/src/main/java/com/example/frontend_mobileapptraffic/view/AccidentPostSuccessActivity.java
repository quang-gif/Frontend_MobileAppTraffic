package com.example.frontend_mobileapptraffic.view;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend_mobileapptraffic.R;

public class AccidentPostSuccessActivity extends AppCompatActivity {
    private ImageButton btnClose;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accident_post_success);

        btnClose = findViewById(R.id.btnClose_ap);

        // Khi nhấn nút 'x' thì đóng màn hình
        btnClose.setOnClickListener(v -> {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            finish();
        });

        // Tự động đóng sau 5 giây
        startCountdown();
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Không cần làm gì
            }

            @Override
            public void onFinish() {
                finish();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
