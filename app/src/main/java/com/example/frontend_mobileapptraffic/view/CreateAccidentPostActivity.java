package com.example.frontend_mobileapptraffic.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.frontend_mobileapptraffic.KeyProvider;
import com.example.frontend_mobileapptraffic.Presenter.AccidentPostPresenter;
import com.example.frontend_mobileapptraffic.R;
import com.example.frontend_mobileapptraffic.api.OpenCageService;
import com.example.frontend_mobileapptraffic.api.OpenCageResponse;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateAccidentPostActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_LOCATION = 200;
    private static final int REQUEST_PICK_PHOTO = 101;

    private EditText etContent;
    private TextView tvTime;
    private AutoCompleteTextView tvAddress;
    private Button btnTakePhoto, btnPost, btnPickPhoto;

    private ImageView ivPreview1, ivPreview2;
    private ImageButton btnRemove1, btnRemove2;
    private android.view.View frameImage1, frameImage2;

    private Uri photoUri;
    private File photoFile;
    private final List<File> photoFiles = new ArrayList<>();

    private String currentAddress = "";
    private String currentTime = "";

    private AccidentPostPresenter presenter;

    // Thêm adapter như field của class
    private android.widget.ArrayAdapter<String> addressAdapter;

    // Custom adapter không filter
    private static class NoFilterArrayAdapter extends android.widget.ArrayAdapter<String> {
        private List<String> items = new ArrayList<>();

        public NoFilterArrayAdapter(android.content.Context context, int resource) {
            super(context, resource);
        }

        @Override
        public void clear() {
            items.clear();
            super.clear();
        }

        @Override
        public void add(String object) {
            items.add(object);
            super.add(object);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public String getItem(int position) {
            return items.get(position);
        }

        @Override
        public android.widget.Filter getFilter() {
            return new android.widget.Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    results.values = items;
                    results.count = items.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    notifyDataSetChanged();
                }
            };
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_accident_post);

        // Khởi tạo view
        etContent = findViewById(R.id.etContent_ap);
        tvAddress = findViewById(R.id.tvAddress_ap);
        tvTime = findViewById(R.id.tvTime_ap);
        btnTakePhoto = findViewById(R.id.btnTakePhoto_ap);
        btnPost = findViewById(R.id.btnPost_ap);
        btnPickPhoto = findViewById(R.id.btnPickPhoto_ap);

        frameImage1 = findViewById(R.id.frameImage_ap1);
        frameImage2 = findViewById(R.id.frameImage_ap2);
        ivPreview1 = findViewById(R.id.ivPreview_ap1);
        ivPreview2 = findViewById(R.id.ivPreview_ap2);
        btnRemove1 = findViewById(R.id.btnRemove_ap1);
        btnRemove2 = findViewById(R.id.btnRemove_ap2);

        presenter = new AccidentPostPresenter(this, null);

        // Khởi tạo adapter
        addressAdapter = new NoFilterArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line
        );
        tvAddress.setAdapter(addressAdapter);

        autoCompleteAddress();

        // Nút chụp ảnh
        btnTakePhoto.setOnClickListener(v -> {
            if (photoFiles.size() >= 2) {
                Toast.makeText(this, "Chỉ được chọn tối đa 2 ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            } else {
                openCamera();
            }
        });

        btnPickPhoto.setOnClickListener(v -> {
            if (photoFiles.size() >= 2) {
                Toast.makeText(this, "Chỉ được chọn tối đa 2 ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), REQUEST_PICK_PHOTO);
        });

        // Nút đăng bài
        btnPost.setOnClickListener(v -> uploadPost());

        // Xóa ảnh 1
        btnRemove1.setOnClickListener(v -> {
            if (!photoFiles.isEmpty()) {
                photoFiles.remove(0);
                frameImage1.setVisibility(android.view.View.GONE);

                // Nếu còn ảnh thứ 2 thì dồn về ảnh 1
                if (photoFiles.size() == 1) {
                    File f = photoFiles.get(0);
                    frameImage1.setVisibility(android.view.View.VISIBLE);
                    Glide.with(this).load(f).into(ivPreview1);
                    frameImage2.setVisibility(android.view.View.GONE);
                }
            }
        });

        // Xóa ảnh 2
        btnRemove2.setOnClickListener(v -> {
            if (photoFiles.size() == 2) {
                photoFiles.remove(1);
                frameImage2.setVisibility(android.view.View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            if (photoFile != null && photoFile.exists()) {
                photoFiles.add(photoFile);

                if (photoFiles.size() == 1) {
                    frameImage1.setVisibility(android.view.View.VISIBLE);
                    Glide.with(this).load(photoFile).into(ivPreview1);
                } else if (photoFiles.size() == 2) {
                    frameImage2.setVisibility(android.view.View.VISIBLE);
                    Glide.with(this).load(photoFile).into(ivPreview2);
                }

                currentTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        .format(new Date());

                String enteredAddress = tvAddress.getText().toString().trim();
                if (enteredAddress.isEmpty() || currentAddress.isEmpty() ||
                        currentAddress.equals("Không tìm thấy địa chỉ")) {
                    getLocationAndAddress();
                } else {
                    // Nếu đã có địa chỉ, chỉ cập nhật thời gian
                    tvTime.setText("Thời gian: " + currentTime);
                }
            }
        }
        else if (requestCode == REQUEST_PICK_PHOTO && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                // Chọn nhiều ảnh
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count && photoFiles.size() < 2; i++) {
                    android.net.Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    File file = getFileFromUri(imageUri);
                    if (file != null) {
                        photoFiles.add(file);
                    }
                }
            } else if (data.getData() != null) {
                // Chọn 1 ảnh
                android.net.Uri imageUri = data.getData();
                File file = getFileFromUri(imageUri);
                if (file != null) {
                    photoFiles.add(file);
                }
            }
            currentTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    .format(new Date());

            // Ngõ 86, Triều Khúc, Thanh Xuân, Hà Nội
            // Cập nhật hiển thị ảnh
            updateImagePreview();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
        if (requestCode == REQUEST_LOCATION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationAndAddress();
        }
    }

    private void openCamera() {
        try {
            photoFile = new File(getExternalFilesDir(
                    android.os.Environment.DIRECTORY_PICTURES),
                    "photo_" + System.currentTimeMillis() + ".jpg");

            photoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    photoFile
            );

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, REQUEST_CAMERA);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể mở camera", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPost() {
        if (photoFiles.isEmpty()) {
            Toast.makeText(this, "Vui lòng chụp ít nhất 1 ảnh", Toast.LENGTH_SHORT).show();
            return;
        }
        if (photoFiles.size() > 2) {
            Toast.makeText(this, "Chỉ được tải lên tối đa 2 ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        String content = etContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi presenter với list ảnh
        presenter.createPost(content, currentAddress, currentTime, photoFiles,
                () -> {
                    Intent intent = new Intent(CreateAccidentPostActivity.this, AccidentPostSuccessActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_SHORT).show()
        );
    }

    private void getLocationAndAddress() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient =
                com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                currentAddress = getAddressFromLocation(lat, lng);

                tvAddress.setText("Địa chỉ: " + currentAddress + "\nLat: " + lat + ", Lng: " + lng);
                tvTime.setText("Thời gian: " + currentTime);
            } else {
                Toast.makeText(this, "Không lấy được vị trí", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAddressFromLocation(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Không tìm thấy địa chỉ";
    }

    private void updateImagePreview() {
        if (photoFiles.size() >= 1) {
            frameImage1.setVisibility(android.view.View.VISIBLE);
            Glide.with(this).load(photoFiles.get(0)).into(ivPreview1);
        }
        if (photoFiles.size() >= 2) {
            frameImage2.setVisibility(android.view.View.VISIBLE);
            Glide.with(this).load(photoFiles.get(1)).into(ivPreview2);
        }
    }

    private File getFileFromUri(android.net.Uri uri) {
        try {
            // Copy file vào cache để upload
            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
            File tempFile = new File(getCacheDir(), "temp_image_" + System.currentTimeMillis() + ".jpg");

            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            return tempFile;
        } catch (Exception e) {
            android.util.Log.e("CreatePost", "Error converting URI to File", e);
            return null;
        }
    }

    private void autoCompleteAddress() {
        tvAddress.setThreshold(1); // Hiện gợi ý sau 1 ký tự

        if (!currentAddress.isEmpty() && !currentAddress.equals("Không tìm thấy địa chỉ")) {
            tvAddress.setText(currentAddress);
        }

        tvAddress.addTextChangedListener(new android.text.TextWatcher() {
            private android.os.Handler handler = new android.os.Handler();
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                runnable = () -> {
                    if (s.length() > 2) {
                        fetchOpenCagePredictions(s.toString());
                    }
                };
                handler.postDelayed(runnable, 400);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        tvAddress.setOnItemClickListener((parent, view, position, id) -> {
            @SuppressWarnings("unchecked")
            android.widget.ArrayAdapter<String> adapter =
                    (android.widget.ArrayAdapter<String>) tvAddress.getAdapter();
            if (adapter != null && position < adapter.getCount()) {
                String selectedAddress = adapter.getItem(position);
                currentAddress = selectedAddress;

                if (currentTime.isEmpty()) {
                    currentTime = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
                            .format(new java.util.Date());
                    tvTime.setText("Thời gian: " + currentTime);
                }
            }
        });
    }

    private void fetchOpenCagePredictions(String query) {
//        android.util.Log.d("OpenCage", "========== FETCH START ==========");
//        android.util.Log.d("OpenCage", "Searching for: " + query);

        okhttp3.logging.HttpLoggingInterceptor logging = new okhttp3.logging.HttpLoggingInterceptor();
        logging.setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BODY);

        okhttp3.OkHttpClient okHttpClient = new okhttp3.OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("https://api.opencagedata.com/")
                .client(okHttpClient)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build();

        // Lấy API key từ native library
        KeyProvider keyProvider = new KeyProvider();
        String apiKey = keyProvider.getOpenCageApiKey();

        OpenCageService service = retrofit.create(OpenCageService.class);

        // Sử dụng apiKey từ KeyProvider thay vì BuildConfig
        retrofit2.Call<OpenCageResponse> call = service.searchAddress(
                query,
                apiKey,
                "vn",
                5
        );

        call.enqueue(new retrofit2.Callback<OpenCageResponse>() {
            @Override
            public void onResponse(retrofit2.Call<OpenCageResponse> call,
                                   retrofit2.Response<OpenCageResponse> response) {
                android.util.Log.d("OpenCage", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    OpenCageResponse body = response.body();
                    android.util.Log.d("OpenCage", "Results count: " +
                            (body.results != null ? body.results.size() : 0));

                    if (body.results != null && !body.results.isEmpty()) {
                        List<String> addresses = new ArrayList<>();
                        for (OpenCageResponse.Result result : body.results) {
                            android.util.Log.d("OpenCage", "Result: " + result.formatted);
                            addresses.add(result.formatted);
                        }

                        runOnUiThread(() -> {
                            // Lấy adapter trực tiếp từ tvAddress thay vì dùng field
                            @SuppressWarnings("unchecked")
                            android.widget.ArrayAdapter<String> adapter =
                                    (android.widget.ArrayAdapter<String>) tvAddress.getAdapter();

                            if (adapter != null) {
                                android.util.Log.d("OpenCage", "Before clear: " + adapter.getCount());
                                adapter.clear();
                                android.util.Log.d("OpenCage", "After clear: " + adapter.getCount());

                                for (String addr : addresses) {
                                    android.util.Log.d("OpenCage", "Adding to adapter: " + addr);
                                    adapter.add(addr);
                                    android.util.Log.d("OpenCage", "Count after add: " + adapter.getCount());
                                }

                                adapter.notifyDataSetChanged();
                                android.util.Log.d("OpenCage", "Final adapter count: " + adapter.getCount());

                                // Đảm bảo AutoCompleteTextView có focus và hiển thị dropdown
                                if (!tvAddress.hasFocus()) {
                                    tvAddress.requestFocus();
                                }
                                tvAddress.showDropDown();
                                android.util.Log.d("OpenCage", "========== FETCH END ==========");
                            } else {
                                android.util.Log.e("OpenCage", "Adapter is null!");
                            }
                        });
                    } else {
                        android.util.Log.e("OpenCage", "Empty results");
                    }
                } else {
                    android.util.Log.e("OpenCage", "Response error: " + response.code() +
                            " - " + response.message());
                    try {
                        android.util.Log.e("OpenCage", "Error body: " +
                                response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<OpenCageResponse> call, Throwable t) {
                android.util.Log.e("OpenCage", "Error fetching predictions", t);
                runOnUiThread(() ->
                        Toast.makeText(CreateAccidentPostActivity.this,
                                "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
