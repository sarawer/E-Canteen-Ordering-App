package com.example.e_canteenorderingapp.student;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_canteenorderingapp.PaymentActivity;
import com.example.e_canteenorderingapp.R;
import com.example.e_canteenorderingapp.data.AppDatabase;
import com.example.e_canteenorderingapp.data.CartDao;
import com.example.e_canteenorderingapp.data.CartItem;

import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartListener {

    private CartDao cartDao;
    private CartAdapter adapter;
    private TextView tvTotal, tvItemCount, btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        RecyclerView rv = findViewById(R.id.rv_cart);
        tvTotal = findViewById(R.id.tv_total);
        tvItemCount = findViewById(R.id.tv_item_count);
        btnBack = findViewById(R.id.btn_back);
        Button btnCheckout = findViewById(R.id.btn_checkout);

        cartDao = AppDatabase.getInstance(getApplicationContext()).cartDao();
        adapter = new CartAdapter(this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Checkout button - navigate to payment
        btnCheckout.setOnClickListener(v -> {
            // Check if cart has items
            if (adapter.getItemCount() > 0) {
                Intent intent = new Intent(this, PaymentActivity.class);
                startActivity(intent);
            }
        });

        // Bottom nav wiring + highlight
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            startActivity(new Intent(this, StudentHomeActivity.class));
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.nav_cart).startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.nav_pulse));
        findViewById(R.id.nav_cart).setAlpha(1f);
        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0, 0);
        });

        load();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh cart each time we come back (e.g., after successful order)
        load();
    }

    private void load() {
        new AsyncTask<Void, Void, List<CartItem>>() {
            Integer total;
            @Override protected List<CartItem> doInBackground(Void... voids) {
                total = cartDao.getTotalCents();
                return cartDao.getAll();
            }
            @Override protected void onPostExecute(List<CartItem> items) {
                adapter.submit(items);
                int cents = total == null ? 0 : total;
                tvTotal.setText("৳" + (cents / 100));
                tvItemCount.setText(items.size() + " Item(s)");
            }
        }.execute();
    }

    @Override public void onIncrement(CartItem item) { updateQty(item, item.quantity + 1); }
    @Override public void onDecrement(CartItem item) { if (item.quantity > 1) updateQty(item, item.quantity - 1); }
    @Override public void onRemove(CartItem item) {
        new AsyncTask<CartItem, Void, Integer>() {
            @Override protected Integer doInBackground(CartItem... it) { return cartDao.delete(it[0]); }
            @Override protected void onPostExecute(Integer rows) { load(); }
        }.execute(item);
    }

    private void updateQty(CartItem item, int q) {
        item.quantity = q;
        new AsyncTask<CartItem, Void, Integer>() {
            @Override protected Integer doInBackground(CartItem... it) { return cartDao.update(it[0]); }
            @Override protected void onPostExecute(Integer rows) { load(); }
        }.execute(item);
    }
}


