package com.example.e_canteenorderingapp.admin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_canteenorderingapp.R;
import com.example.e_canteenorderingapp.data.AppDatabase;
import com.example.e_canteenorderingapp.data.Order;
import com.example.e_canteenorderingapp.data.OrderItem;
import com.example.e_canteenorderingapp.util.FormatUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminSalesReportActivity extends AppCompatActivity {

    private TextView tvTotalOrders, tvTotalRevenue;
    private RecyclerView rvItemsSold;
    private SalesItemsAdapter itemsAdapter;
    private Button btnDateToday, btnDateYesterday, btnDatePick, btnDateRange;
    private TextView tvSelectedDate, tvEmpty;
    private long rangeStartMs = 0L, rangeEndMs = Long.MAX_VALUE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sales_report);

        tvTotalOrders = findViewById(R.id.tv_total_orders);
        tvTotalRevenue = findViewById(R.id.tv_total_revenue);
        rvItemsSold = findViewById(R.id.rv_items_sold);
        rvItemsSold.setLayoutManager(new LinearLayoutManager(this));
        itemsAdapter = new SalesItemsAdapter();
        rvItemsSold.setAdapter(itemsAdapter);


        btnDatePick = findViewById(R.id.btn_date_pick);
        btnDateRange = findViewById(R.id.btn_date_range);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        tvEmpty = findViewById(R.id.tv_empty);

        if (btnDateToday != null) btnDateToday.setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            rangeStartMs = cal.getTimeInMillis();
            rangeEndMs = now;
            if (tvSelectedDate != null) tvSelectedDate.setText(getString(R.string.selected_prefix, getString(R.string.today)));
            loadReport();
        });
        if (btnDateYesterday != null) btnDateYesterday.setOnClickListener(v -> {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            // yesterday 00:00 to 23:59:59
            cal.add(java.util.Calendar.DAY_OF_YEAR, -1);
            rangeStartMs = cal.getTimeInMillis();
            cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
            rangeEndMs = cal.getTimeInMillis() - 1;
            if (tvSelectedDate != null) tvSelectedDate.setText(getString(R.string.selected_prefix, getString(R.string.yesterday)));
            loadReport();
        });
        if (btnDatePick != null) btnDatePick.setOnClickListener(v -> {
            final java.util.Calendar cal = java.util.Calendar.getInstance();
            android.app.DatePickerDialog dp = new android.app.DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        java.util.Calendar chosen = java.util.Calendar.getInstance();
                        chosen.set(year, month, dayOfMonth, 0, 0, 0);
                        chosen.set(java.util.Calendar.MILLISECOND, 0);
                        rangeStartMs = chosen.getTimeInMillis();
                        chosen.add(java.util.Calendar.DAY_OF_YEAR, 1);
                        rangeEndMs = chosen.getTimeInMillis() - 1;
                        if (tvSelectedDate != null) tvSelectedDate.setText(getString(R.string.selected_prefix, FormatUtils.formatDate(rangeStartMs)));
                        loadReport();
                    },
                    cal.get(java.util.Calendar.YEAR),
                    cal.get(java.util.Calendar.MONTH),
                    cal.get(java.util.Calendar.DAY_OF_MONTH)
            );
            dp.show();
        });

        if (btnDateRange != null) btnDateRange.setOnClickListener(v -> {
            final java.util.Calendar cal = java.util.Calendar.getInstance();
            android.app.DatePickerDialog from = new android.app.DatePickerDialog(
                    this,
                    (view, y1, m1, d1) -> {
                        java.util.Calendar start = java.util.Calendar.getInstance();
                        start.set(y1, m1, d1, 0, 0, 0);
                        start.set(java.util.Calendar.MILLISECOND, 0);
                        android.app.DatePickerDialog to = new android.app.DatePickerDialog(
                                this,
                                (view2, y2, m2, d2) -> {
                                    java.util.Calendar end = java.util.Calendar.getInstance();
                                    end.set(y2, m2, d2, 23, 59, 59);
                                    end.set(java.util.Calendar.MILLISECOND, 999);
                                    rangeStartMs = start.getTimeInMillis();
                                    rangeEndMs = end.getTimeInMillis();
                                    if (tvSelectedDate != null) tvSelectedDate.setText(getString(R.string.selected_prefix, FormatUtils.formatRange(rangeStartMs, rangeEndMs)));
                                    loadReport();
                                },
                                cal.get(java.util.Calendar.YEAR),
                                cal.get(java.util.Calendar.MONTH),
                                cal.get(java.util.Calendar.DAY_OF_MONTH)
                        );
                        to.show();
                    },
                    cal.get(java.util.Calendar.YEAR),
                    cal.get(java.util.Calendar.MONTH),
                    cal.get(java.util.Calendar.DAY_OF_MONTH)
            );
            from.setTitle("Select start date");
            from.show();
        });

        loadReport();
    }

    // chip selection removed; using date buttons now

    private void loadReport() {
        new AsyncTask<Void, Void, Object[]>() {
            @Override
            protected Object[] doInBackground(Void... voids) {
                int totalOrders = 0;
                long totalRevenueCents = 0;
                try {
                    totalOrders = AppDatabase.getInstance(getApplicationContext())
                            .orderDao().getTotalOrdersBetween(rangeStartMs, rangeEndMs);
                } catch (Exception ignored) {}
                try {
                    int sum = AppDatabase.getInstance(getApplicationContext())
                            .orderDao().getRevenueBetween(rangeStartMs, rangeEndMs);
                    totalRevenueCents = sum;
                } catch (Exception ignored) {}

                // Aggregate items: name -> [qty, revenueCents]
                Map<String, int[]> agg = new HashMap<>();
                try {
                    List<Order> orders = AppDatabase.getInstance(getApplicationContext()).orderDao().getAllOrders();
                    for (Order o : orders) {
                        if (o.createdAt < rangeStartMs || o.createdAt > rangeEndMs) continue;
                        List<OrderItem> items = AppDatabase.getInstance(getApplicationContext()).orderDao().getItemsForOrder(o.orderNumber);
                        for (OrderItem it : items) {
                            int[] arr = agg.getOrDefault(it.name, new int[]{0,0});
                            arr[0] += it.quantity;
                            arr[1] += it.unitPriceCents * it.quantity;
                            agg.put(it.name, arr);
                        }
                    }
                } catch (Exception ignored) {}

                List<SalesItemsAdapter.Row> rows = new ArrayList<>();
                for (Map.Entry<String, int[]> e : agg.entrySet()) {
                    rows.add(new SalesItemsAdapter.Row(e.getKey(), e.getValue()[0], e.getValue()[1]));
                }

                return new Object[]{ totalOrders, totalRevenueCents, rows };
            }

            @Override
            protected void onPostExecute(Object[] data) {
                int totalOrders = (int) data[0];
                long totalRevenueCents = (long) data[1];
                @SuppressWarnings("unchecked") List<SalesItemsAdapter.Row> rows = (List<SalesItemsAdapter.Row>) data[2];

                tvTotalOrders.setText(String.valueOf(totalOrders));
                tvTotalRevenue.setText(FormatUtils.formatBdt(totalRevenueCents));
                itemsAdapter.submit(rows);
                if (tvEmpty != null) tvEmpty.setVisibility(rows.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        }.execute();
    }
}


