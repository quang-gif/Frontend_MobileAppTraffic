package com.example.duan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CaptchaActivity extends AppCompatActivity {

    private EditText edtCaptcha;
    private ImageView imgCaptcha;
    private MaterialButton btnXacNhan;


    private String loaiXe, bienSo, sessionId, captchaImage;

    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha);

        // Nhận dữ liệu từ MainActivity
        loaiXe = getIntent().getStringExtra("LOAI_XE");
        bienSo = getIntent().getStringExtra("BIEN_SO");
        sessionId = getIntent().getStringExtra("SESSION_ID");
        captchaImage = getIntent().getStringExtra("CAPTCHA_IMAGE");

        edtCaptcha = findViewById(R.id.edtCaptcha);
        imgCaptcha = findViewById(R.id.imgCaptcha);
        btnXacNhan = findViewById(R.id.btnXacNhan);

        // Hiển thị ảnh captcha (base64 → bitmap)
        if (captchaImage != null && !captchaImage.isEmpty()) {
            try {
                String base64Data = captchaImage;

                // Nếu chuỗi có prefix "data:image/png;base64," thì bỏ đi
                if (base64Data.startsWith("data")) {
                    base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
                }

                byte[] decoded = Base64.decode(base64Data, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);

                if (bmp != null) {
                    imgCaptcha.setImageBitmap(bmp);
                } else {
                    Toast.makeText(this, "Decode xong nhưng Bitmap null", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi hiển thị captcha: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }


        // Xử lý nút xác nhận
        btnXacNhan.setOnClickListener(v -> {
            String input = edtCaptcha.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã xác thực", Toast.LENGTH_SHORT).show();
                return;
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);

            // Gọi API submit captcha
            new Thread(() -> {
                try {
                    JSONObject body = new JSONObject();
                    body.put("sessionId", sessionId);
                    body.put("plate", bienSo);
                    body.put("type", loaiXe);
                    body.put("captcha", input);

                    JSONObject resp = ApiClient.post("http://10.0.2.2:8080/api/check/submit", body);

                    // Trả kết quả về MainActivity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("RESP_JSON", resp.toString());
                    resultIntent.putExtra("VEHICLE_TYPE", getIntent().getStringExtra("LOAI_XE"));
                    setResult(RESULT_OK, resultIntent);
                    finish();

                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }
}
