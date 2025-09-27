package com.example.frontend_mobileapptraffic.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend_mobileapptraffic.Presenter.LoginPresenter;
import com.example.frontend_mobileapptraffic.R;

public class loginactivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword, edtEmail;

    private Button btnLogin;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); //
        Log.d("LOGIN_ACTIVITY", "onCreate");
        // ⚠️ đảm bảo đúng layout
        // ánh xạ view
        edtUsername = findViewById(R.id.emailOrPhone);
        edtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.loginButton);;
        TextView signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(loginactivity.this, useractivity.class);
            startActivity(intent);
        });
        // khởi tạo presenter
        presenter = new LoginPresenter(this);
        // gắn sự kiện click
        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Hãy nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            presenter.loginUser(username, password);
        });
    }
}
