package com.example.frontend_mobileapptraffic.Presenter;

import static com.example.frontend_mobileapptraffic.BuildConfig.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.frontend_mobileapptraffic.view.homeactivity;
import com.example.frontend_mobileapptraffic.view.homeactivity;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class LoginPresenter {
    private Context context;
    private Api api;

    // Khai báo API ngay trong Presenter
    interface Api {
        @POST("login")   // endpoint login backend
        Call<ResponseBody> login(@Body JsonObject request);
    }

    public LoginPresenter(Context context) {
        this.context = context;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)   // lấy từ BuildConfig
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(Api.class);
    }

    public void loginUser(String username, String password) {
        // tạo JSON request
        JsonObject req = new JsonObject();
        req.addProperty("username", username);
        req.addProperty("password", password);

        api.login(req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String respStr = response.body().string();
                        JSONObject json = new JSONObject(respStr);

                        boolean success = json.optBoolean("success", false);
                        String message = json.optString("message", "No message");

                        if (success) {
                            // đăng nhập thành công, lấy token
                            String token = json.optString("data", "");
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            Log.d("API_LOGIN", "Token: " + token);

                            // ✅ Lưu token vào SharedPreferences
                            SharedPreferences prefs = context.getSharedPreferences("token", Context.MODE_PRIVATE);
                            prefs.edit().putString("JWT_TOKEN", token).apply();

                            // Chuyển sang HomeActivity
                            Intent intent = new Intent(context, homeactivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                        Log.e("API_LOGIN", "Error code: " + response.code());
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Parse lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("API_LOGIN", "Parse error", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Kết nối thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_LOGIN", "Call failed", t);
            }
        });
    }
}
