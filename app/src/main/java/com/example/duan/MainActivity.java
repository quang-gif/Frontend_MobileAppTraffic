package com.example.duan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText edtPlate;

    private MaterialButton btnTraCuu;
    private Spinner spType;

    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private static final String API_BASE = "http://10.0.2.2:8080"; // dùng emulator

    private final ActivityResultLauncher<Intent> captchaLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String json = result.getData().getStringExtra("RESP_JSON");
                    String type = result.getData().getStringExtra("VEHICLE_TYPE");
                    openResult(json, type);
                }
            });

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        edtPlate = findViewById(R.id.edtPlate);
        spType = findViewById(R.id.spinnerLoaiXe);
        btnTraCuu = findViewById(R.id.btnTraCuu);

        // dữ liệu cho Spinner
        String[] loaiXe = {"oto", "moto"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                loaiXe
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

        // mặc định chọn "oto"
        spType.setSelection(0);

        btnTraCuu.setOnClickListener(v -> doCheck());
    }

    private void doCheck() {
        String plate = edtPlate.getText().toString().trim();
        String type = spType.getSelectedItem().toString();

        if (plate.isEmpty()) {
            Toast.makeText(this, "Nhập biển số", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("plate", plate);
                body.put("type", type);

                JSONObject resp = ApiClient.post(API_BASE + "/api/check/start", body);

                runOnUiThread(() -> {
                    if (resp.optBoolean("captchaRequired", false)) {
                        // Nếu cần captcha → mở CaptchaActivity
                        Intent intent = new Intent(MainActivity.this, CaptchaActivity.class);
                        intent.putExtra("LOAI_XE", type);
                        intent.putExtra("BIEN_SO", plate);
                        intent.putExtra("SESSION_ID", resp.optString("sessionId"));
                        intent.putExtra("CAPTCHA_IMAGE", resp.optString("captchaImage"));
                        captchaLauncher.launch(intent);
                    } else {
                        openResult(resp.toString(),type);

                    }
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void openResult(String json, String type) {
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra("RESP_JSON", json);
        intent.putExtra("PLATE", edtPlate.getText().toString().trim());
        intent.putExtra("VEHICLE_TYPE", type); // Thêm loại xe
        startActivity(intent);
    }

}
