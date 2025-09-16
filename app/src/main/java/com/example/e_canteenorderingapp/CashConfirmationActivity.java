package com.example.e_canteenorderingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CashConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "CashConfirmationActivity";
    private TextView tvOrderNumber, tvOrderDate, tvOrderTime, tvLocation, tvTotalAmount, tvDeliveryTime;
    private Button btnEditOrder, btnConfirmOrder;
    private double totalAmount = 0.0;
    private String tableNumber = "";
    private String orderNumber = "";
    
    // Debug flag
    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_confirmation);

        Log.d(TAG, "CashConfirmationActivity onCreate started");

        try {
            // Get data from previous screen (EXACT SAME AS OTPVerificationActivity)
            Intent intent = getIntent();
            if (intent != null) {
                totalAmount = intent.getDoubleExtra("total_amount", 0.0);
                tableNumber = intent.getStringExtra("table_number");
                
                // DETAILED DEBUG - CASH FLOW STEP 2
                Log.d(TAG, "=== CASH FLOW DEBUG - STEP 2 ===");
                Log.d(TAG, "Received total_amount: " + totalAmount);
                Log.d(TAG, "Received table_number: '" + tableNumber + "'");
                Log.d(TAG, "tableNumber length: " + (tableNumber != null ? tableNumber.length() : "null"));
                Log.d(TAG, "tableNumber class: " + (tableNumber != null ? tableNumber.getClass().getSimpleName() : "null"));
                
                // Debug all intent extras
                Log.d(TAG, "ALL INTENT EXTRAS:");
                if (intent.getExtras() != null) {
                    for (String key : intent.getExtras().keySet()) {
                        Object value = intent.getExtras().get(key);
                        Log.d(TAG, "  '" + key + "' = '" + value + "' (type: " + (value != null ? value.getClass().getSimpleName() : "null") + ")");
                    }
                } else {
                    Log.e(TAG, "Intent extras is NULL!");
                }
                Log.d(TAG, "================================");
            } else {
                Log.e(TAG, "Intent is NULL!");
            }

            initializeViews();
            setupData();
            setupClickListeners();

            Log.d(TAG, "onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() throws Exception {
        Log.d(TAG, "Initializing views");

        tvOrderNumber = findViewById(R.id.tv_order_number);
        if (tvOrderNumber == null) throw new Exception("tv_order_number not found");

        tvOrderDate = findViewById(R.id.tv_order_date);
        if (tvOrderDate == null) throw new Exception("tv_order_date not found");

        tvOrderTime = findViewById(R.id.tv_order_time);
        if (tvOrderTime == null) throw new Exception("tv_order_time not found");

        tvLocation = findViewById(R.id.tv_location);
        if (tvLocation == null) throw new Exception("tv_location not found");

        tvTotalAmount = findViewById(R.id.tv_total_amount);
        if (tvTotalAmount == null) throw new Exception("tv_total_amount not found");

        tvDeliveryTime = findViewById(R.id.tv_delivery_time);
        if (tvDeliveryTime == null) throw new Exception("tv_delivery_time not found");

        btnEditOrder = findViewById(R.id.btn_edit_order);
        if (btnEditOrder == null) throw new Exception("btn_edit_order not found");

        btnConfirmOrder = findViewById(R.id.btn_confirm_order);
        if (btnConfirmOrder == null) throw new Exception("btn_confirm_order not found");

        Log.d(TAG, "All views initialized successfully");
    }

    private void setupData() {
        try {
            Log.d(TAG, "Setting up data");

            // Generate order number
            orderNumber = generateOrderNumber();
            tvOrderNumber.setText(orderNumber);

            // Set current date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);

            String currentDate = dateFormat.format(new Date());
            String currentTime = timeFormat.format(new Date());

            tvOrderDate.setText(currentDate);
            tvOrderTime.setText(currentTime);

            // Set table number under location if available
            TextView tvTableLabel = findViewById(R.id.tv_table_number);
            if (tvTableLabel != null) {
                if (tableNumber != null && !tableNumber.isEmpty()) {
                    tvTableLabel.setText(tableNumber);
                } else {
                    tvTableLabel.setText("—");
                }
            }

            // Set total amount
            tvTotalAmount.setText("৳" + String.format("%.2f", totalAmount));

            // Set delivery time based on order size
            if (totalAmount > 50) {
                tvDeliveryTime.setText("20-35min");
            } else {
                tvDeliveryTime.setText("15-30min");
            }

            Log.d(TAG, "Data setup completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error setting up data: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            Log.d(TAG, "Setting up click listeners");

            btnEditOrder.setOnClickListener(v -> {
                Log.d(TAG, "Edit Order button clicked");
                // Go back to cart to edit order
                finish();
            });

            btnConfirmOrder.setOnClickListener(v -> {
                Log.d(TAG, "Confirm Order button clicked");
                processOrderConfirmation();
            });

            Log.d(TAG, "Click listeners setup successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
        }
    }

    private void processOrderConfirmation() {
        try {
            Log.d(TAG, "Processing order confirmation");

            // Show confirmation dialog
            Toast.makeText(this, "Order confirmed! Processing...", Toast.LENGTH_SHORT).show();

            // Disable buttons during processing
            btnConfirmOrder.setEnabled(false);
            btnConfirmOrder.setText("Processing...");

            // Simulate order processing
            btnConfirmOrder.postDelayed(() -> {
                try {
                    Log.d(TAG, "Order processing completed, navigating to success screen");

                    // Navigate to order success screen (EXACT SAME AS OTPVerificationActivity)
                    Intent intent = new Intent(this, OrderSuccessActivity.class);
                    intent.putExtra("order_number", orderNumber);
                    intent.putExtra("total_amount", totalAmount);
                    intent.putExtra("payment_method", "CASH");
                    intent.putExtra("table_number", tableNumber);
                    
                    // DETAILED DEBUG - CASH FLOW STEP 3
                    Log.d(TAG, "=== CASH FLOW DEBUG - STEP 3 ===");
                    Log.d(TAG, "Sending to OrderSuccessActivity:");
                    Log.d(TAG, "  order_number: " + orderNumber);
                    Log.d(TAG, "  total_amount: " + totalAmount);
                    Log.d(TAG, "  payment_method: CASH");
                    Log.d(TAG, "  table_number: '" + tableNumber + "'");
                    Log.d(TAG, "  tableNumber length: " + (tableNumber != null ? tableNumber.length() : "null"));
                    Log.d(TAG, "================================");
                    
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                } catch (Exception e) {
                    Log.e(TAG, "Error processing order: " + e.getMessage(), e);
                    btnConfirmOrder.setEnabled(true);
                    btnConfirmOrder.setText("Confirm Order");
                }
            }, 2000);

            Log.d(TAG, "Order processing started");

        } catch (Exception e) {
            Log.e(TAG, "Error generating order number: " + e.getMessage(), e);
            btnConfirmOrder.setEnabled(true);
            btnConfirmOrder.setText("Confirm Order");
        }
    }

    private String generateOrderNumber() {
        try {
            // Generate order number in format: EC-YYYY-XXX
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
            String currentYear = yearFormat.format(new Date());
            
            // For demo purposes, generate a random 3-digit number
            int orderSeq = (int) (Math.random() * 900) + 100; // Random number 100-999
            
            return "EC-" + currentYear + "-" + orderSeq;
            
        } catch (Exception e) {
            Log.e(TAG, "Error generating order number: " + e.getMessage(), e);
            return "EC-2024-001"; // Fallback
        }
    }
}