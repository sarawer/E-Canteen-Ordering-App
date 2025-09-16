package com.example.e_canteenorderingapp.admin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_canteenorderingapp.R;
import com.example.e_canteenorderingapp.data.AppDatabase;
import com.example.e_canteenorderingapp.data.FoodDao;
import com.example.e_canteenorderingapp.data.FoodItem;

import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity implements AdminFoodAdapter.OnAdminAction {

    private RecyclerView recyclerView;
    private AdminFoodAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        recyclerView = findViewById(R.id.rv_admin_food);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminFoodAdapter(this);
        recyclerView.setAdapter(adapter);

        Button btnAdd = findViewById(R.id.btn_add_food);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AdminAddFoodActivity.class)));
        }

        Button btnSave = findViewById(R.id.btn_save_changes);
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                Toast.makeText(AdminDashboardActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
            });
        }
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
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
                adapter.submit(foodItems);
            }
        }.execute();
    }

    @Override
    public void onDelete(FoodItem item) {
        new AsyncTask<FoodItem, Void, Integer>() {
            @Override
            protected Integer doInBackground(FoodItem... items) {
                return AppDatabase.getInstance(getApplicationContext()).foodDao().delete(items[0]);
            }

            @Override
            protected void onPostExecute(Integer rows) {
                Toast.makeText(AdminDashboardActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                loadData();
            }
        }.execute(item);
    }
}



