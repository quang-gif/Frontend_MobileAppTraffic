package com.example.frontend_mobileapptraffic.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.frontend_mobileapptraffic.model.TrafficPost;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

import static com.example.frontend_mobileapptraffic.BuildConfig.BASE_URL;

public class TrafficPostPresenter {

    private static final String TAG = "TRAFFIC_POST";

    private final Context context;
    private final TrafficPostView view;
    private final Api api;

    // --------- View interface ---------
    public interface TrafficPostView {
        void onPostsLoaded(List<TrafficPost> posts);
        void onPostsLoadError(String errorMessage);
        void onPostLiked(int position);
        void onPostReported(int position);
        void onPostDeleted(int position);
    }

    // --------- API interface ---------
    interface Api {
        @GET("TrafficPost")
        Call<ResponseBody> getPosts(@Header("Authorization") String token);

        @POST("TrafficPost/Like/{trafficPostId}")
        Call<ResponseBody> likePost(
                @Header("Authorization") String token,
                @Path("trafficPostId") long trafficPostId
        );

        @Multipart
        @POST("TrafficPost")
        Call<ResponseBody> createPost(
                @Header("Authorization") String token,
                @Part("TrafficPost") RequestBody trafficPostJson,
                @Part List<MultipartBody.Part> files
        );
    }

    // --------- Constructor ---------
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

    // --------- Load posts ---------
    public void loadPosts() {
        String token = getToken();
        if (token == null) {
            if (view != null) view.onPostsLoadError("Chưa đăng nhập!");
            return;
        }

        api.getPosts("Bearer " + token).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String respStr = response.body().string();
                        JSONObject json = new JSONObject(respStr);

                        if (json.optBoolean("success", false)) {
                            JSONArray dataArray = json.optJSONArray("data");
                            List<TrafficPost> posts = parsePosts(dataArray);
                            if (view != null) view.onPostsLoaded(posts);
                        } else {
                            String msg = json.optString("message", "API thất bại");
                            if (view != null) view.onPostsLoadError(msg);
                        }
                    } else {
                        if (view != null) view.onPostsLoadError("Lỗi server: " + response.code());
                        Log.e(TAG, "Error code: " + response.code());
                    }
                } catch (Exception e) {
                    if (view != null) view.onPostsLoadError("Parse lỗi: " + e.getMessage());
                    Log.e(TAG, "Parse error", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (view != null) view.onPostsLoadError("Kết nối thất bại: " + t.getMessage());
                Log.e(TAG, "Call failed", t);
            }
        });
    }

    // --------- Create post ---------
    public void createPost(String content, String location, String timestamp,
                           List<File> imageFiles, Runnable onSuccess, Consumer<String> onError) {

        String token = getToken();
        if (token == null) {
            onError.accept("Chưa đăng nhập!");
            return;
        }

        // Chuẩn bị danh sách file
        List<MultipartBody.Part> parts = new ArrayList<>();
        if (imageFiles != null) {
            for (File file : imageFiles) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
                parts.add(MultipartBody.Part.createFormData("file", file.getName(), requestFile));
            }
        }

        // JSON metadata
        String trafficPostJson = "{"
                + "\"content\":\"" + content + "\","
                + "\"location\":\"" + location + "\","
                + "\"timestamp\":\"" + timestamp + "\""
                + "}";

        RequestBody trafficPostBody = RequestBody.create(
                MediaType.parse("application/json"),
                trafficPostJson
        );

        api.createPost("Bearer " + token, trafficPostBody, parts).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    onSuccess.run();
                    loadPosts(); // refresh list
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

    // --------- Like post ---------
    public void toggleLike(long postId, int position) {
        String token = getToken();
        if (token == null) {
            if (view != null) view.onPostsLoadError("Chưa đăng nhập!");
            return;
        }

        api.likePost("Bearer " + token, postId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (view != null) view.onPostLiked(position);
                    loadPosts(); // refresh like count
                } else {
                    if (view != null) view.onPostsLoadError("Lỗi server khi like: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (view != null) view.onPostsLoadError("Kết nối thất bại khi like: " + t.getMessage());
            }
        });
    }

    // --------- Report & Delete (mock local) ---------
    public void reportPost(int position) {
        if (view != null) view.onPostReported(position);
    }

    public void deletePost(int position) {
        if (view != null) view.onPostDeleted(position);
    }

    // --------- Helper ---------
    private String getToken() {
        SharedPreferences prefs = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        return prefs.getString("JWT_TOKEN", null);
    }

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
                post.setLikeTotal(postJson.optInt("likeTotal", 0));

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
            Log.e(TAG, "Parse error", e);
        }
        return posts;
    }
}
