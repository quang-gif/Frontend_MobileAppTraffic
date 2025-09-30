package com.example.frontend_mobileapptraffic.Presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.frontend_mobileapptraffic.BuildConfig;
import com.example.frontend_mobileapptraffic.model.ReportTrafficRequest;

import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class ReportPresenter {

    private static final String TAG = "REPORT";
    private final Context context;
    private final Api api;

    // ----- API interface -----
    interface Api {
        @POST("report")
        Call<ResponseBody> reportPost(
                @Header("Authorization") String token,
                @Body ReportTrafficRequest request
        );
    }

    // ----- Constructor -----
    public ReportPresenter(Context context) {
        this.context = context;

        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        api = retrofit.create(Api.class);
    }

    // ----- Gọi API report -----
    public void reportPost(long postId, String reason,
                           Runnable onSuccess, Consumer<String> onError) {
        SharedPreferences prefs = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        String token = prefs.getString("JWT_TOKEN", null);

        if (token == null) {
            onError.accept("Bạn chưa đăng nhập!");
            return;
        }

        ReportTrafficRequest request = new ReportTrafficRequest(postId, reason);

        api.reportPost("Bearer " + token, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    onSuccess.run();
                } else {
                    onError.accept("Lỗi server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onError.accept("Kết nối thất bại: " + t.getMessage());
            }
        });
    }
}
