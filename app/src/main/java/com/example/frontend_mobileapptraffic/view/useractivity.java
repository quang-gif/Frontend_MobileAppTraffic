package com.example.frontend_mobileapptraffic.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend_mobileapptraffic.Presenter.RegisterPresenter;
import com.example.frontend_mobileapptraffic.R;

public class useractivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword, edtUsername;
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
        btnRegister = findViewById(R.id.registerButton);

        // khởi tạo presenter
        presenter = new RegisterPresenter(this);

        // sự kiện click nút đăng ký
        btnRegister.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();
            String username = edtUsername.getText().toString().trim();

            presenter.registerUser(email, pass, username);
        });
    }
}
