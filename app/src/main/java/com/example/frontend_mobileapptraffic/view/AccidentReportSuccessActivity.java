package com.example.frontend_mobileapptraffic.view;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend_mobileapptraffic.R;

public class AccidentReportSuccessActivity extends AppCompatActivity {
    private ImageButton btnClose;
    private TextView txtTitle;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accident_post_success);
        txtTitle = findViewById(R.id.textView_ap);
        btnClose = findViewById(R.id.btnClose_ap);
        txtTitle.setText("Báo cáo thành công! Chờ duyệt.");

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
        countDownTimer = new CountDownTimer(3000, 1000) {
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
