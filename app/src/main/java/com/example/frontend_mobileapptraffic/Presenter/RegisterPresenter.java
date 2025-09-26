package com.example.frontend_mobileapptraffic.Presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.frontend_mobileapptraffic.model.UserRequest;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class RegisterPresenter {
    private Context context;
    private ApiService apiService;

    // Định nghĩa API ngay trong Presenter
    interface ApiService {
        @POST("users")   // endpoint backend (ví dụ: http://192.168.0.148:8080/users)
        Call<JsonObject> register(@Body UserRequest request);
    }

    public RegisterPresenter(Context context) {
        this.context = context;

        Retrofit retrofit = new Retrofit.Builder()
                // ⚠️ Nếu chạy trên emulator dùng "http://10.0.2.2:8080/"
                // Nếu chạy trên điện thoại thật dùng IP LAN máy backend
                .baseUrl("http://192.168.0.148:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public void registerUser(String email, String password, String username) {
        UserRequest request = new UserRequest(email, password, username);

        apiService.register(request).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String message = response.body().get("message").getAsString();
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(context, "Parse lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("API_RESPONSE", "Parse error", e);
                    }
                } else {
                    Toast.makeText(context, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("API_RESPONSE", "Server error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, "Kết nối thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Call failed", t);
            }
        });
    }
}
