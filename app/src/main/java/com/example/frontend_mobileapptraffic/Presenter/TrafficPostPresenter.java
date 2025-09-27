package com.example.frontend_mobileapptraffic.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.frontend_mobileapptraffic.model.TrafficPost;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;

import static com.example.frontend_mobileapptraffic.BuildConfig.BASE_URL;

public class TrafficPostPresenter {

    private static final String TAG = "TRAFFIC_POST";

    private Context context;
    private TrafficPostView view;
    private Api api;



    // View interface để Activity implement
    public interface TrafficPostView {
        void onPostsLoaded(List<TrafficPost> posts);
        void onPostsLoadError(String errorMessage);
        void onPostLiked(int position);
        void onPostReported(int position);
        void onPostDeleted(int position);
    }

    // Retrofit API interface
    interface Api {
        @GET("TrafficPost")   // 👉 endpoint backend bạn build
        Call<ResponseBody> getPosts(
                @Header("Authorization") String token
        );
    }

    public TrafficPostPresenter(Context context, TrafficPostView view) {
        this.context = context;
        this.view = view;

        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        api = retrofit.create(Api.class);
    }

    // Load danh sách bài đăng
    public void loadPosts() {
        SharedPreferences prefs = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        String token = prefs.getString("JWT_TOKEN", null);

        if (token == null) {
            view.onPostsLoadError("Chưa đăng nhập hoặc chưa có token!");
            return;
        }

        api.getPosts("Bearer " + token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String respStr = response.body().string();
                        JSONObject json = new JSONObject(respStr);

                        boolean success = json.optBoolean("success", false);
                        if (success) {
                            JSONArray dataArray = json.optJSONArray("data");
                            List<TrafficPost> posts = parsePosts(dataArray);
                            view.onPostsLoaded(posts);
                        } else {
                            String msg = json.optString("message", "API trả về thất bại");
                            view.onPostsLoadError("Lỗi: " + msg);
                        }
                    } else {
                        view.onPostsLoadError("Lỗi server: " + response.code());
                        Log.e(TAG, "Error code: " + response.code());
                    }
                } catch (Exception e) {
                    view.onPostsLoadError("Parse lỗi: " + e.getMessage());
                    Log.e(TAG, "Parse error", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                view.onPostsLoadError("Kết nối thất bại: " + t.getMessage());
                Log.e(TAG, "Call failed", t);
            }
        });
    }

    // Parse JSON -> List<TrafficPost>
    private List<TrafficPost> parsePosts(JSONArray dataArray) {
        List<TrafficPost> posts = new ArrayList<>();
        if (dataArray == null) return posts;

        try {
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject postJson = dataArray.getJSONObject(i);
                TrafficPost post = new TrafficPost();

                post.setIdPost(postJson.optLong("idPost"));
                post.setContent(postJson.optString("content", ""));
                post.setLocation(postJson.optString("location", ""));
                post.setTimestamp(postJson.optString("timestamp", ""));
                post.setUsername(postJson.optString("username", ""));

                // Lấy danh sách ảnh
                JSONArray imagesArray = postJson.optJSONArray("imageUrls");
                if (imagesArray != null) {
                    List<String> imageUrls = new ArrayList<>();
                    for (int j = 0; j < imagesArray.length(); j++) {
                        imageUrls.add(imagesArray.getString(j));
                    }
                    post.setImageUrls(imageUrls);
                }

                posts.add(post);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing posts", e);
        }

        return posts;
    }

    // Các hành động với bài viết (sau có thể call API riêng)
    public void likePost(int position) {
        view.onPostLiked(position);
    }

    public void reportPost(int position) {
        view.onPostReported(position);
    }

    public void deletePost(int position) {
        view.onPostDeleted(position);
    }
}
