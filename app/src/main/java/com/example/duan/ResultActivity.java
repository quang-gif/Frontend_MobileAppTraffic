package com.example.duan;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_checkviolation);

        TextView tvPlate   = findViewById(R.id.tvLicensePlate);
        TextView tvTotal   = findViewById(R.id.tvTotalViolations);
        TextView tvPaid    = findViewById(R.id.tvPaidCount);
        TextView tvUnpaid  = findViewById(R.id.tvUnpaidCount);
        LinearLayout container = findViewById(R.id.containerViolations);

        String jsonStr = getIntent().getStringExtra("RESP_JSON");
        String inputPlate = getIntent().getStringExtra("PLATE"); // biển số gốc từ MainActivity
        String vehicleType = getIntent().getStringExtra("VEHICLE_TYPE");

        try {
            JSONObject root = new JSONObject(jsonStr);
            JSONArray arr   = root.optJSONArray("violations");

            // Lấy biển số ưu tiên từ JSON root -> Intent -> fallback arr[0]
            String plate = root.optString("plate", "");
            if (plate.isEmpty() && inputPlate != null) plate = inputPlate;
            if (plate.isEmpty() && arr != null && arr.length() > 0) {
                plate = arr.getJSONObject(0).optString("licensePlate", "--");
            }
            if (plate.isEmpty()) plate = "--";
            plate = formatLicensePlate(plate, vehicleType);

            int total = root.optInt("totalViolations", arr != null ? arr.length() : 0);

            if (vehicleType == null) vehicleType = "oto"; // mặc định

            TextView tvPlatePrefix = findViewById(R.id.tvPlatePrefix);
            if (vehicleType.equalsIgnoreCase("moto")) {
                tvPlatePrefix.setText("Xe máy có biển số");
            } else {
                tvPlatePrefix.setText("Xe ô tô có biển số");
            }

            tvPlate.setText(plate);
            tvTotal.setText("Phát hiện " + total + " lỗi vi phạm");

            int paid = 0, unpaid = 0;
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    String status = arr.getJSONObject(i).optString("status", "");
                    if (status.toLowerCase().contains("đã")) paid++;
                    else unpaid++;
                }
            }
            tvPaid.setText(paid + " đã xử phạt");
            tvUnpaid.setText(unpaid + " chưa xử phạt");

            // Danh sách vi phạm
            container.removeAllViews();
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject v = arr.getJSONObject(i);
                    View card = buildViolationCard(i + 1, v);
                    container.addView(card);
                }
            }
            else {
                // Thêm thông báo không có vi phạm
                TextView noViolation = new TextView(this);
                noViolation.setText("Không có lỗi vi phạm nào được phát hiện.");
                noViolation.setTextSize(16);
                noViolation.setTextColor(Color.GRAY);
                noViolation.setGravity(View.TEXT_ALIGNMENT_CENTER);
                noViolation.setPadding(0, 50, 0, 50);
                container.addView(noViolation);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Parse lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private View buildViolationCard(int index, JSONObject v) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_violation_card, null);

        ((TextView) card.findViewById(R.id.tvTitle))
                .setText("Lỗi " + index);

        ((TextView) card.findViewById(R.id.tvLicensePlate))
                .setText("Biển số: " + v.optString("licensePlate", "--"));

        ((TextView) card.findViewById(R.id.tvPlateColor))
                .setText("Màu biển: " + v.optString("plateColor", "--"));

        ((TextView) card.findViewById(R.id.tvVehicleType))
                .setText("Loại phương tiện: " + v.optString("vehicleType", "--"));

        ((TextView) card.findViewById(R.id.tvTime))
                .setText("Thời gian vi phạm: " + v.optString("violationTime", "--"));

        ((TextView) card.findViewById(R.id.tvLocation))
                .setText(v.optString("violationLocation", "--"));

        ((TextView) card.findViewById(R.id.tvBehavior))
                .setText(v.optString("violationBehavior", "--"));

        ((TextView) card.findViewById(R.id.tvDetectedBy))
                .setText( v.optString("detectedBy", "--"));

        // Nơi giải quyết
        TextView tvPlaces = card.findViewById(R.id.tvPaymentPlaces);
        JSONArray places = v.optJSONArray("paymentPlaces");
        if (places != null && places.length() > 0) {
            List<String> list = new ArrayList<>();
            for (int j = 0; j < places.length(); j++) {
                list.add(places.optString(j));
            }
            tvPlaces.setText(join(list));
        } else {
            tvPlaces.setText("—");
        }

        // Trạng thái
        TextView txtStatus = card.findViewById(R.id.tvStatus);
        String status = v.optString("status", "Chưa rõ");
        txtStatus.setText(status);

        LinearLayout headerLayout = card.findViewById(R.id.headerLayout);

        if (status.equalsIgnoreCase("Chưa xử phạt")) {
            txtStatus.setBackgroundResource(R.drawable.bg_status_unpaid);
            txtStatus.setTextColor(Color.parseColor("#D32F2F"));
            headerLayout.setBackgroundResource(R.drawable.bg_header_unpaid);
        } else {
            txtStatus.setBackgroundResource(R.drawable.bg_status_paid);
            txtStatus.setTextColor(Color.parseColor("#2E7D32"));
            headerLayout.setBackgroundResource(R.drawable.bg_header_paid);
        }

        return card;
    }

    private String join(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append("• ").append(s).append("\n");
        }
        return sb.toString().trim();
    }
    private String formatLicensePlate(String plate, String vehicleType) {
        if (plate == null || plate.equals("--")) {
            return plate;
        }

        // Loại bỏ khoảng trắng và chuyển thành chữ hoa
        String cleanPlate = plate.replaceAll("\\s", "").toUpperCase();

        if (vehicleType != null && vehicleType.equalsIgnoreCase("moto")) {
            // Định dạng cho xe máy: XX-YZ.ABCD hoặc XXX-YZ.ABC
            if (cleanPlate.length() >= 6) {
                try {
                    // Kiểu 1: 30Y81819 -> 30-Y8.1819 (8 ký tự)
                    if (cleanPlate.length() == 8) {
                        return cleanPlate.substring(0, 2) + "-" +
                                cleanPlate.substring(2, 4) + "." +
                                cleanPlate.substring(4);
                    }
                    // Kiểu 2: 17B359175 -> 17B3-591.75 (9 ký tự)
                    else {
                        return cleanPlate.substring(0, 4) + "-" +
                                cleanPlate.substring(4, 7) + "." +
                                cleanPlate.substring(7);
                    }

                } catch (Exception e) {
                    return plate;
                }
            }
        } else {

                // Kiểu: 51C91820 -> 51C-918.20
                return cleanPlate.substring(0, 3) + "-" +
                        cleanPlate.substring(3, 6) + "." +
                        cleanPlate.substring(6);

        }

        return plate;
    }

}
