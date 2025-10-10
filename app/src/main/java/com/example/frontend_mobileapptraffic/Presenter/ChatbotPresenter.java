package com.example.frontend_mobileapptraffic.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONObject;

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

import static com.example.frontend_mobileapptraffic.BuildConfig.BASE_URL;

public class ChatbotPresenter {
    private Context context;
    private Api api;

    // Interface API khai báo ngay trong presenter
    interface Api {
        @POST("chatbot/ask")
        Call<ResponseBody> askQuestion(@Header("Authorization") String token, @Body JsonObject body);
    }

    public ChatbotPresenter(Context context) {
        this.context = context;

        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        api = retrofit.create(Api.class);
    }

    public void sendQuestion(String question, ChatbotCallback callback) {
        // Lấy token từ SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        String token = prefs.getString("JWT_TOKEN", null);

        if (token == null) {
            Toast.makeText(context, "Chưa đăng nhập hoặc chưa có token!", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject body = new JsonObject();
        body.addProperty("question", question);

        api.askQuestion("Bearer " + token, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String respStr = response.body().string();
                        JSONObject json = new JSONObject(respStr);

                        boolean success = json.optBoolean("success", false);
                        String answer = json.optString("data", "Không có câu trả lời");

                        if (success) {
                            callback.onSuccess(answer);
                        } else {
                            callback.onError("Lỗi: " + json.optString("message", "API trả về thất bại"));
                        }
                    } else {
                        callback.onError("Lỗi server: " + response.code());
                        Log.e("CHATBOT", "Error code: " + response.code());
                    }
                } catch (Exception e) {
                    callback.onError("Parse lỗi: " + e.getMessage());
                    Log.e("CHATBOT", "Parse error", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError("Kết nối thất bại: " + t.getMessage());
                Log.e("CHATBOT", "Call failed", t);
            }
        });
    }

    // Callback để báo kết quả về Activity
    public interface ChatbotCallback {
        void onSuccess(String answer);
        void onError(String errorMessage);
    }
}
