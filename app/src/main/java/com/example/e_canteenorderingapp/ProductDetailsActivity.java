package com.example.e_canteenorderingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_canteenorderingapp.data.AppDatabase;
import com.example.e_canteenorderingapp.data.CartItem;
import com.example.e_canteenorderingapp.data.FoodItem;

public class ProductDetailsActivity extends AppCompatActivity {

    private FoodItem foodItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Get food item data from intent (you'll need to pass this)
        long foodId = getIntent().getLongExtra("food_id", 0);
        String name = getIntent().getStringExtra("food_name");
        String desc = getIntent().getStringExtra("food_desc");
        String imageRef = getIntent().getStringExtra("food_image");
        int priceCents = getIntent().getIntExtra("food_price", 0);

        if (foodId > 0) {
            foodItem = new FoodItem(name, desc, imageRef, priceCents);
            foodItem.id = foodId;
        }

        ImageView ivProduct = findViewById(R.id.iv_product);
        TextView tvTitle = findViewById(R.id.tv_product_title);
        TextView tvRating = findViewById(R.id.tv_rating);
        TextView tvDescription = findViewById(R.id.tv_description);
        TextView tvPrice = findViewById(R.id.tv_price);
        TextView btnBack = findViewById(R.id.btn_back);
        Button btnAddToCart = findViewById(R.id.btn_add_to_cart);

        // Set data
        if (foodItem != null) {
            tvTitle.setText(foodItem.name);
            tvDescription.setText(foodItem.description);
            tvPrice.setText("৳" + (foodItem.priceCents / 100));

            // Load image
            Context ctx = this;
            if (foodItem.imageRef != null && (foodItem.imageRef.startsWith("content://") || foodItem.imageRef.startsWith("file://"))) {
                com.bumptech.glide.Glide.with(ctx)
                        .load(android.net.Uri.parse(foodItem.imageRef))
                        .centerCrop()
                        .placeholder(R.drawable.burger_combo)
                        .into(ivProduct);
            } else {
                int resId = ctx.getResources().getIdentifier(foodItem.imageRef, "drawable", ctx.getPackageName());
                if (resId == 0) resId = R.drawable.burger_combo;
                ivProduct.setImageResource(resId);
            }
        }

        btnBack.setOnClickListener(v -> finish());

        btnAddToCart.setOnClickListener(v -> {
            if (foodItem != null) {
                addToCart(foodItem);
            }
        });
    }

    private void addToCart(FoodItem item) {
        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                CartItem ci = new CartItem(item.id, item.name, item.imageRef, item.priceCents, 1);
                return AppDatabase.getInstance(getApplicationContext()).cartDao().insert(ci);
            }

            @Override
            protected void onPostExecute(Long id) {
                Toast.makeText(ProductDetailsActivity.this, "Added to cart!", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}
