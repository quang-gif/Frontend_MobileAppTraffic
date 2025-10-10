package com.example.frontend_mobileapptraffic.Presenter;

import static com.example.frontend_mobileapptraffic.BuildConfig.BASE_URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.frontend_mobileapptraffic.model.AccidentPost;

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

public class AccidentPostPresenter {
    public static final String TAG = "ACCIDENT_POST";

    public final Context context;
    public final AccidentPostView view;
    public final Api api;

    public interface AccidentPostView {
        void onPostsLoaded(List<AccidentPost> posts);
        void onPostsLoadError(String errorMessage);
        void onPostLiked(int position);
        void onPostReported(int position);
        void onPostDeleted(int position);
    }

    interface Api {
        @GET("accident-posts")
        Call<ResponseBody> getPosts(@Header("Authorization") String token);

        @POST("accident-posts/like/{accidentPostId}")
        Call<ResponseBody> likePost(
                @Header("Authorization") String token,
                @Path("accidentPostId") long accidentPostId
        );

        @Multipart
        @POST("accident-posts")
        Call<ResponseBody> createPost(
                @Header("Authorization") String token,
                @Part("AccidentPost") RequestBody accidentPostJson,
                @Part List<MultipartBody.Part> files
        );
    }

    public AccidentPostPresenter(Context context, AccidentPostView view) {
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

    private String getToken() {
        SharedPreferences prefs = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        return prefs.getString("JWT_TOKEN", null);
    }

    private List<AccidentPost> parsePosts(JSONArray dataArray) {
        List<AccidentPost> posts = new ArrayList<>();
        if (dataArray == null) return posts;
        try {
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject postJson = dataArray.getJSONObject(i);
                AccidentPost post = new AccidentPost();

                post.setIdAcPost(postJson.optLong("id"));
                post.setContent(postJson.optString("content", ""));
                post.setLocation(postJson.optString("location", ""));
                post.setUsername(postJson.optString("username", ""));
                post.setLikeTotal(postJson.optInt("likeTotal", 0));
                post.setCreatedAt(postJson.optString("createdAt", ""));

                JSONArray imagesArray = postJson.optJSONArray("imageUrls");
                if (imagesArray != null) {
                    List<String> imageUrls = new ArrayList<>();
                    for (int j = 0; j < imagesArray.length(); j++) {
                        String imageUrl = imagesArray.getString(j);
                        imageUrls.add(imageUrl);
                        Log.d(TAG, "Image URL [" + i + "][" + j + "]: " + imageUrl);
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
                            List<AccidentPost> posts = parsePosts(dataArray);
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

    public void createPost(String content, String location, String createdAt,
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
                parts.add(MultipartBody.Part.createFormData("files", file.getName(), requestFile));
            }
        }

        // JSON metadata
        String accidentPostJson = "{"
                + "\"content\":\"" + content + "\","
                + "\"location\":\"" + location + "\","
                + "\"createdAt\":\"" + createdAt + "\""
                + "}";

        RequestBody accidentPostBody = RequestBody.create(
                MediaType.parse("application/json"),
                accidentPostJson
        );

        api.createPost("Bearer " + token, accidentPostBody, parts).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    onSuccess.run();
                    loadPosts(); // refresh list
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("AccidentPost", "Error: " + response.code() + " - " + errorBody);
                        onError.accept("Lỗi server: " + response.code() + " - " + errorBody);
                    } catch (Exception e) {
                        onError.accept("Lỗi server: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onError.accept("Kết nối thất bại: " + t.getMessage());
            }
        });
    }

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

    public void reportPost(int position) {
        if (view != null) view.onPostReported(position);
    }

    public void deletePost(int position) {
        if (view != null) view.onPostDeleted(position);
    }

}
