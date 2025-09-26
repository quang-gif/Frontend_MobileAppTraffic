package com.example.frontend_mobileapptraffic.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend_mobileapptraffic.Presenter.LoginPresenter;
import com.example.frontend_mobileapptraffic.R;

public class homeactivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        edtEmail = findViewById(R.id.emailOrPhone);
        edtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.loginButton);
        presenter = new LoginPresenter();
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();

        });
    }
}
