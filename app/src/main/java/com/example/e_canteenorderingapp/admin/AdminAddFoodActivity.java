package com.example.e_canteenorderingapp.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_canteenorderingapp.R;
import com.example.e_canteenorderingapp.data.AppDatabase;
import com.example.e_canteenorderingapp.data.FoodItem;

public class AdminAddFoodActivity extends AppCompatActivity {

    private EditText etName, etDesc, etPrice, etImageRef;
    private ImageView ivPreview;
    private Uri pickedImageUri;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_food);

        etName = findViewById(R.id.et_name);
        etDesc = findViewById(R.id.et_desc);
        etPrice = findViewById(R.id.et_price);
        etImageRef = findViewById(R.id.et_image_ref);
        ivPreview = findViewById(R.id.iv_preview);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnPick = findViewById(R.id.btn_pick_image);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        pickedImageUri = result.getData().getData();
                        if (pickedImageUri != null) {
                            try {
                                getContentResolver().takePersistableUriPermission(
                                        pickedImageUri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                );
                            } catch (Exception ignored) {}
                            ivPreview.setImageURI(pickedImageUri);
                            etImageRef.setText(pickedImageUri.toString());
                        }
                    }
                }
        );

        btnPick.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    private void save() {
        String name = etName.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String imageRef = etImageRef.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pickedImageUri != null) {
            imageRef = pickedImageUri.toString();
        }
        if (TextUtils.isEmpty(imageRef)) imageRef = "burger_combo"; // fallback drawable

        int taka;
        try {
            taka = Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }
        int priceCents = taka * 100;

        FoodItem item = new FoodItem(name, desc, imageRef, priceCents);

        new AsyncTask<FoodItem, Void, Long>() {
            @Override
            protected Long doInBackground(FoodItem... items) {
                return AppDatabase.getInstance(getApplicationContext()).foodDao().insert(items[0]);
            }

            @Override
            protected void onPostExecute(Long id) {
                Toast.makeText(AdminAddFoodActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        }.execute(item);
    }
}


