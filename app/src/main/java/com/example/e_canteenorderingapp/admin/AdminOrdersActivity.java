package com.example.e_canteenorderingapp.admin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_canteenorderingapp.R;
import com.example.e_canteenorderingapp.data.AppDatabase;
import com.example.e_canteenorderingapp.data.OrderStatus;

import java.util.ArrayList;
import java.util.List;

public class AdminOrdersActivity extends AppCompatActivity implements AdminOrdersAdapter.OnOrderActionListener {

    private RecyclerView recyclerView;
    private AdminOrdersAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        recyclerView = findViewById(R.id.rv_admin_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrdersAdapter(this);
        recyclerView.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        new AsyncTask<Void, Void, List<OrderStatus>>() {
            @Override
            protected List<OrderStatus> doInBackground(Void... voids) {
                try {
                    // Fallback: show orders table if exists, else statuses empty
                    // Prefer Orders table when available
                    List<OrderStatus> statuses = new ArrayList<>();
                    // Derive statuses from Orders if present
                    List<com.example.e_canteenorderingapp.data.Order> orders =
                            AppDatabase.getInstance(getApplicationContext()).orderDao().getAllOrders();
                    for (com.example.e_canteenorderingapp.data.Order o : orders) {
                        statuses.add(new OrderStatus(o.orderNumber, o.status, o.tableNumber, o.createdAt));
                    }
                    return statuses;
                } catch (Exception e) {
                    return new ArrayList<>();
                }
            }

            @Override
            protected void onPostExecute(List<OrderStatus> orderStatuses) {
                adapter.submit(orderStatuses);
            }
        }.execute();
    }

    @Override
    public void onStatusChanged(OrderStatus order, int newStatus) {
        new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... params) {
                try {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    OrderStatus os = (OrderStatus) params[0];
                    int status = (int) params[1];

                    int updatedOrders = 0;
                    int updatedStatuses = 0;
                    try {
                        updatedOrders = db.orderDao().updateStatus(os.orderNumber, status);
                    } catch (Exception ignored) {}
                    try {
                        updatedStatuses = db.orderStatusDao()
                                .updateStatus(os.orderNumber, status, System.currentTimeMillis());
                    } catch (Exception ignored) {}
                    return (updatedOrders > 0) || (updatedStatuses > 0);
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean ok) {
                if (ok != null && ok) {
                    Toast.makeText(AdminOrdersActivity.this, "Status updated", Toast.LENGTH_SHORT).show();
                    loadOrders();
                } else {
                    Toast.makeText(AdminOrdersActivity.this, "Nothing updated", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(order, newStatus);
    }
}


