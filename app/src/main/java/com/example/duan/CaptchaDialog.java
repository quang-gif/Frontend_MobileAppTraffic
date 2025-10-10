package com.example.duan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.json.JSONObject;

import java.util.function.Consumer;

public class CaptchaDialog extends DialogFragment {
    private String sessionId, plate, type, captchaImage;
    private EditText edtCaptcha;
    private ImageView imgCaptcha;
    private Button btnSubmit;
    private Consumer<String> onResultListener;

    public static CaptchaDialog newInstance(String sessionId, String plate, String type, String captchaImage) {
        CaptchaDialog d = new CaptchaDialog();
        Bundle b = new Bundle();
        b.putString("sessionId", sessionId);
        b.putString("plate", plate);
        b.putString("type", type);
        b.putString("captchaImage", captchaImage);
        d.setArguments(b);
        return d;
    }

    public void setOnResultListener(Consumer<String> listener) {
        this.onResultListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_captcha, container, false);
        edtCaptcha = v.findViewById(R.id.edtCaptcha);
        imgCaptcha = v.findViewById(R.id.imgCaptcha);
        btnSubmit = v.findViewById(R.id.btnXacNhan);

        sessionId = getArguments().getString("sessionId");
        plate = getArguments().getString("plate");
        type = getArguments().getString("type");
        captchaImage = getArguments().getString("captchaImage");

        // load ảnh captcha (base64 -> bitmap)
        byte[] decoded = Base64.decode(captchaImage, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
        imgCaptcha.setImageBitmap(bmp);

        btnSubmit.setOnClickListener(v1 -> sendCaptcha());
        return v;
    }

    private void sendCaptcha() {
        String captcha = edtCaptcha.getText().toString().trim();
        if (captcha.isEmpty()) {
            Toast.makeText(getContext(), "Nhập captcha", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("sessionId", sessionId);
                body.put("plate", plate);
                body.put("type", type);
                body.put("captcha", captcha);

                JSONObject resp = ApiClient.post("http://10.0.2.2:8080/api/check/submit", body);

                requireActivity().runOnUiThread(() -> {
                    if (onResultListener != null) {
                        onResultListener.accept(resp.toString());
                    }
                    dismiss();
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}

