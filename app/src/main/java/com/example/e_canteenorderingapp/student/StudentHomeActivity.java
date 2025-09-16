package com.example.e_canteenorderingapp.student;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_canteenorderingapp.R;
import com.example.e_canteenorderingapp.data.AppDatabase;
import com.example.e_canteenorderingapp.data.FoodDao;
import com.example.e_canteenorderingapp.data.FoodItem;

import java.util.List;

public class StudentHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private android.widget.TextView tvEmpty;
    private EditText etSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        recyclerView = findViewById(R.id.rv_food);
        tvEmpty = findViewById(R.id.tv_empty);
        etSearch = findViewById(R.id.et_search);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));
        adapter = new FoodAdapter();
        adapter.setListener(item -> {
            // Add to cart table
            new android.os.AsyncTask<Void, Void, Long>() {
                @Override
                protected Long doInBackground(Void... voids) {
                    com.example.e_canteenorderingapp.data.CartDao cartDao = com.example.e_canteenorderingapp.data.AppDatabase.getInstance(getApplicationContext()).cartDao();
                    com.example.e_canteenorderingapp.data.CartItem ci = new com.example.e_canteenorderingapp.data.CartItem(
                            item.id,
                            item.name,
                            item.imageRef,
                            item.priceCents,
                            1
                    );
                    return cartDao.insert(ci);
                }

                @Override
                protected void onPostExecute(Long id) {
                    android.widget.Toast.makeText(StudentHomeActivity.this, "Added to cart: " + item.name, android.widget.Toast.LENGTH_SHORT).show();
                }
            }.execute();
        });
        recyclerView.setAdapter(adapter);

        loadData();

        // Search filter
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterByName(s.toString());
                tvEmpty.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Wire bottom nav
        findViewById(R.id.nav_home).startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.nav_pulse));
        findViewById(R.id.nav_cart).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, CartActivity.class));
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, com.example.e_canteenorderingapp.student.ProfileActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    private void loadData() {
        new AsyncTask<Void, Void, List<FoodItem>>() {
            @Override
            protected List<FoodItem> doInBackground(Void... voids) {
                FoodDao dao = AppDatabase.getInstance(getApplicationContext()).foodDao();
                return dao.getAll();
            }

            @Override
            protected void onPostExecute(List<FoodItem> foodItems) {
                boolean isEmpty = (foodItems == null || foodItems.isEmpty());
                if (isEmpty) {
                    SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
                    boolean seeded = sp.getBoolean("seeded", false);
                    if (!seeded) {
                        seedSampleData(sp);
                        return;
                    }
                }
                adapter.submit(foodItems);
                tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            }
        }.execute();
    }

    private void seedSampleData(SharedPreferences sp) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                FoodDao dao = AppDatabase.getInstance(getApplicationContext()).foodDao();
                dao.insert(new FoodItem("Burger Combo", "Juicy burger with fries and drink.", "burger_combo", 120 * 100));
                dao.insert(new FoodItem("Fries", "Crispy french fries.", "fries_circle", 40 * 100));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                sp.edit().putBoolean("seeded", true).apply();
                loadData();
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit app?")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Exit", (dialog, which) -> finishAffinity())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}


