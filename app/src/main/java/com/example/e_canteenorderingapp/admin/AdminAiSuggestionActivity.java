package com.example.e_canteenorderingapp.admin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_canteenorderingapp.R;
import com.example.e_canteenorderingapp.data.AppDatabase;
import com.example.e_canteenorderingapp.data.OrderItem;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAiSuggestionActivity extends AppCompatActivity {

    private TextView tvSummary, tvTopItems, tvSuggestions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_ai_suggestion);

        tvSummary = findViewById(R.id.tv_summary);
        tvTopItems = findViewById(R.id.tv_top_items);
        tvSuggestions = findViewById(R.id.tv_suggestions);

        runAnalysis();
    }

    private void runAnalysis() {
        new AsyncTask<Void, Void, String[]>() {
            @Override
            protected String[] doInBackground(Void... voids) {
                try {
                    // Aggregate order items
                    List<com.example.e_canteenorderingapp.data.Order> orders = AppDatabase.getInstance(getApplicationContext()).orderDao().getAllOrders();
                    Map<String, Integer> nameToQty = new HashMap<>();
                    int totalOrders = orders.size();
                    int totalItems = 0;
                    for (com.example.e_canteenorderingapp.data.Order o : orders) {
                        List<OrderItem> items = AppDatabase.getInstance(getApplicationContext()).orderDao().getItemsForOrder(o.orderNumber);
                        for (OrderItem it : items) {
                            totalItems += it.quantity;
                            nameToQty.put(it.name, nameToQty.getOrDefault(it.name, 0) + it.quantity);
                        }
                    }

                    // Top 3 items
                    List<Map.Entry<String, Integer>> entries = new java.util.ArrayList<>(nameToQty.entrySet());
                    Collections.sort(entries, Comparator.comparingInt(Map.Entry::getValue));
                    Collections.reverse(entries);
                    StringBuilder top = new StringBuilder();
                    int limit = Math.min(3, entries.size());
                    for (int i = 0; i < limit; i++) {
                        Map.Entry<String, Integer> e = entries.get(i);
                        top.append((i+1)).append(". ").append(e.getKey()).append(" — ").append(e.getValue()).append(" orders\n");
                    }

                    // Simple suggestions
                    String suggestion;
                    if (limit > 0) {
                        String bestSeller = entries.get(0).getKey();
                        suggestion = "Consider stocking more of '" + bestSeller + "', running a combo offer, and highlighting it on Home.";
                    } else {
                        suggestion = "No orders yet. Drive orders with a promo banner and starter discounts.";
                    }

                    String summary = "Total orders: " + totalOrders + "\nTotal items sold: " + totalItems;
                    return new String[]{ summary, top.toString(), suggestion };
                } catch (Exception e) {
                    return new String[]{ "Analysis failed: " + e.getMessage(), "", "" };
                }
            }

            @Override
            protected void onPostExecute(String[] result) {
                tvSummary.setText(result[0]);
                tvTopItems.setText(result[1].isEmpty() ? "No top items yet" : result[1]);
                tvSuggestions.setText(result[2]);
            }
        }.execute();
    }
}


