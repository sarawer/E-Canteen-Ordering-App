package com.example.e_canteenorderingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class OrderSuccessActivity extends AppCompatActivity {

    private static final String TAG = "OrderSuccessActivity";
    private Button btnBackToMenu;
    
    private String orderNumber = "";
    private double totalAmount = 0.0;
    private String paymentMethod = "";
    private String tableNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        Log.d(TAG, "OrderSuccessActivity onCreate started");
        
        try {
            // Get data from previous screen
            Intent intent = getIntent();
            if (intent != null) {
                orderNumber = intent.getStringExtra("order_number");
                totalAmount = intent.getDoubleExtra("total_amount", 0.0);
                paymentMethod = intent.getStringExtra("payment_method");
                tableNumber = intent.getStringExtra("table_number");
                
                Log.d(TAG, "Received data - Order: " + orderNumber + ", Amount: " + totalAmount + 
                      ", Payment: " + paymentMethod + ", Table: " + tableNumber);
            }
            
            initializeViews();
            setupData();
            setupClickListeners();
            
            Log.d(TAG, "OrderSuccessActivity onCreate completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing success screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() throws Exception {
        Log.d(TAG, "Initializing views");
        btnBackToMenu = findViewById(R.id.btn_back_to_menu);
        if (btnBackToMenu == null) throw new Exception("btn_back_to_menu not found");
        Log.d(TAG, "All views initialized successfully");
    }

    private void setupData() {
        Log.d(TAG, "Success screen requires no dynamic data now");
    }

    private void setupClickListeners() {
        try {
            Log.d(TAG, "Setting up click listeners");
            
            btnBackToMenu.setOnClickListener(v -> {
                Log.d(TAG, "Back to Menu button clicked");
                // Clear cart then navigate back to student home
                try {
                    com.example.e_canteenorderingapp.data.AppDatabase db = com.example.e_canteenorderingapp.data.AppDatabase.getInstance(getApplicationContext());
                    java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            // Persist order + items if we have the data
                            if (db != null) {
                                int totalCents = (int) Math.round(totalAmount * 100.0);
                                android.content.SharedPreferences sp = getSharedPreferences("users", MODE_PRIVATE);
                                String userId = sp.getString("session_user_id", "");
                                String studentName = sp.getString("user_" + userId + "_name", "");

                                com.example.e_canteenorderingapp.data.Order ord = new com.example.e_canteenorderingapp.data.Order(
                                        orderNumber == null ? String.valueOf(System.currentTimeMillis()) : orderNumber,
                                        System.currentTimeMillis(),
                                        tableNumber,
                                        paymentMethod,
                                        totalCents,
                                        studentName,
                                        20,
                                        0
                                );
                                try { db.orderDao().insertOrder(ord); } catch (Exception ignored2) {}

                                java.util.List<com.example.e_canteenorderingapp.data.CartItem> cart = null;
                                try { cart = db.cartDao().getAll(); } catch (Exception ignored3) {}
                                if (cart != null && !cart.isEmpty()) {
                                    java.util.List<com.example.e_canteenorderingapp.data.OrderItem> items = new java.util.ArrayList<>();
                                    for (com.example.e_canteenorderingapp.data.CartItem ci : cart) {
                                        items.add(new com.example.e_canteenorderingapp.data.OrderItem(ord.orderNumber, ci.foodId, ci.name, ci.unitPriceCents, ci.quantity));
                                    }
                                    try { db.orderDao().insertItems(items); } catch (Exception ignored4) {}
                                }

                                // Also record status row for compatibility
                                try {
                                    db.orderStatusDao().upsert(new com.example.e_canteenorderingapp.data.OrderStatus(ord.orderNumber, 0, tableNumber, System.currentTimeMillis()));
                                } catch (Exception ignored5) {}

                                // Clear cart
                                try { db.cartDao().clear(); } catch (Exception ignored6) {}
                            }
                        } catch (Exception ignored) {}
                    });
                } catch (Exception ignored) {}
                Intent intent = new Intent(this, com.example.e_canteenorderingapp.student.StudentHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
            
            Log.d(TAG, "Click listeners setup successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
        }
    }
}
