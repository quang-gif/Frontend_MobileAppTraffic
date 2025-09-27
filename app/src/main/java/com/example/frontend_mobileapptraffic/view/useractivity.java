package com.example.frontend_mobileapptraffic.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend_mobileapptraffic.Presenter.RegisterPresenter;
import com.example.frontend_mobileapptraffic.R;

public class useractivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword, edtUsername, edtConfirmPass;
    private Button btnRegister;
    private RegisterPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // ánh xạ view
        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);
        edtUsername = findViewById(R.id.fullName);
        edtConfirmPass = findViewById(R.id.confirmPassword);
        btnRegister = findViewById(R.id.registerButton);
        TextView loginNow = findViewById(R.id.loginNow);
        TextView termsTextView = findViewById(R.id.termsAndConditions); // id của "Điều khoản và Điều kiện"
        CheckBox agreeCheckBox = findViewById(R.id.agreeTerms); // id của CheckBox

        termsTextView.setOnClickListener(v -> {
            String terms = "Điều khoản và Điều kiện sử dụng ứng dụng TrafficConnect:\n\n"
                    + "1. Người dùng cần cung cấp thông tin chính xác khi đăng ký.\n"
                    + "2. Không được sử dụng ứng dụng cho mục đích vi phạm pháp luật.\n"
                    + "3. Dữ liệu (bao gồm điểm đen tai nạn, tình hình giao thông, tra cứu phạt nguội) chỉ mang tính chất tham khảo.\n"
                    + "4. Người dùng chịu trách nhiệm về nội dung câu hỏi và thông tin cung cấp.\n"
                    + "5. Quản trị viên có quyền khóa tài khoản nếu phát hiện vi phạm.\n"
                    + "6. Khi sử dụng ứng dụng, người dùng đồng ý với việc lưu trữ token và dữ liệu liên quan để đảm bảo trải nghiệm.\n"
                    + "7. Chúng tôi có thể cập nhật điều khoản và điều kiện bất kỳ lúc nào.\n\n"
                    + "👉 Vui lòng đọc kỹ trước khi tiếp tục.";

            new AlertDialog.Builder(useractivity.this)
                    .setTitle("Điều khoản và Điều kiện")
                    .setMessage(terms)
                    .setCancelable(false)
                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                        agreeCheckBox.setChecked(true); // tự động tích vào CheckBox
                        dialog.dismiss();
                    })
                    .setNegativeButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        loginNow.setOnClickListener(v -> {
            Intent intent = new Intent(useractivity.this, loginactivity.class);
            startActivity(intent);
        });
        // khởi tạo presenter
        presenter = new RegisterPresenter(this);

        // sự kiện click nút đăng ký
        btnRegister.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();
            String username = edtUsername.getText().toString().trim();
            String confirmPass = edtConfirmPass.getText().toString().trim();
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirmPass)){
                Toast.makeText(this, "Hãy nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Mật khẩu bạn nhập không đồng nhất", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!agreeCheckBox.isChecked()){
                Toast.makeText(this, "Hãy đồng ý với điều khoản và điều kiện", Toast.LENGTH_SHORT).show();
                return;
            }
            presenter.registerUser(email, pass, username);
        });
    }
}
