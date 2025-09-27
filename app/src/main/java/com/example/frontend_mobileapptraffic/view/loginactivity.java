package com.example.frontend_mobileapptraffic.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
        // khởi tạo presenter
        presenter = new LoginPresenter(this);

        // gắn sự kiện click
        btnLogin.setOnClickListener(v -> {
            Log.d("LOGIN_CLICK", "Nút Login đã bấm");
            Toast.makeText(this, "Đã click nút login", Toast.LENGTH_SHORT).show();
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            presenter.loginUser(username, password);
        });
    }
}
