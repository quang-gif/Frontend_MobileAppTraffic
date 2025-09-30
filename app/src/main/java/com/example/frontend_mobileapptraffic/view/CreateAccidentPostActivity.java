package com.example.frontend_mobileapptraffic.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.frontend_mobileapptraffic.Presenter.AccidentPostPresenter;
import com.example.frontend_mobileapptraffic.R;

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
    private TextView tvAddress, tvTime;
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

                getLocationAndAddress();
            }
        }
        else if (requestCode == REQUEST_PICK_PHOTO && data != null) {
            // Xử lý ảnh từ thư viện
            Uri imageUri = data.getData();
            if (imageUri != null) {
                File file = new File(getRealPathFromURI(imageUri));
                photoFiles.add(file);

                if (photoFiles.size() == 1) {
                    frameImage1.setVisibility(android.view.View.VISIBLE);
                    Glide.with(this).load(file).into(ivPreview1);
                } else if (photoFiles.size() == 2) {
                    frameImage2.setVisibility(android.view.View.VISIBLE);
                    Glide.with(this).load(file).into(ivPreview2);
                }
            }
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
        presenter.createPost(content, currentAddress, photoFiles,
                () -> {
                    Toast.makeText(this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
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

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }
}
