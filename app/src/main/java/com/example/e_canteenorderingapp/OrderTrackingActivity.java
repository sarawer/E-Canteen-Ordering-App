package com.example.e_canteenorderingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class OrderTrackingActivity extends AppCompatActivity {

    private static final String TAG = "OrderTrackingActivity";
    private TextView tvOrderNumber, tvAmount, tvTableNumber;
    private TextView tvStatusDelivered, tvStatusText, stepConfirmed, stepPreparing, stepOutForDelivery, stepDelivered;
    private android.widget.ProgressBar progressTracking;
    private Button btnViewReceipt, btnOrderAgain;
    private TextView btnBack;
    
    private String orderNumber = "";
    private double totalAmount = 0.0;
    private String paymentMethod = "";
    private String tableNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        Log.d(TAG, "OrderTrackingActivity onCreate started");
        
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
            
            Log.d(TAG, "OrderTrackingActivity onCreate completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing tracking screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() throws Exception {
        Log.d(TAG, "Initializing views");
        
        tvOrderNumber = findViewById(R.id.tv_order_number);
        if (tvOrderNumber == null) throw new Exception("tv_order_number not found");
        
        tvAmount = findViewById(R.id.tv_amount);
        if (tvAmount == null) throw new Exception("tv_amount not found");
        
        tvTableNumber = findViewById(R.id.tv_table_number);
        if (tvTableNumber == null) throw new Exception("tv_table_number not found");
        //tvStatusDelivered = findViewById(R.id.tv_status_delivered); // optional in new layout
        tvStatusText = findViewById(R.id.tv_status_text);
        stepConfirmed = findViewById(R.id.step_confirmed);
        stepPreparing = findViewById(R.id.step_preparing);
        stepOutForDelivery = findViewById(R.id.step_out_for_delivery);
        stepDelivered = findViewById(R.id.step_delivered);
        progressTracking = findViewById(R.id.progress_tracking);
        
        // Back button (top-left)
        btnBack = findViewById(R.id.btn_back);
        
        // Optional receipt button may not exist in current layout
        btnViewReceipt = findViewById(getResources().getIdentifier("btn_view_receipt", "id", getPackageName()));
        
        btnOrderAgain = findViewById(R.id.btn_order_again);
        if (btnOrderAgain == null) throw new Exception("btn_order_again not found");
        
        Log.d(TAG, "All views initialized successfully");
    }

    private void setupData() {
        try {
            Log.d(TAG, "Setting up data");
            
            // Set order number
            if (orderNumber != null && !orderNumber.isEmpty()) {
                tvOrderNumber.setText(orderNumber);
            }
            
            // Set amount
            tvAmount.setText("৳" + String.format("%.2f", totalAmount));
            
            // Set table number
            if (tableNumber != null && !tableNumber.isEmpty()) {
                tvTableNumber.setText(tableNumber);
                if (tvStatusDelivered != null) tvStatusDelivered.setText("Delivered to Table #" + tableNumber);
            }

            // Start simulated tracking updates
            startProgressSimulation();
            
            Log.d(TAG, "Data setup completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up data: " + e.getMessage(), e);
        }
    }

    private void startProgressSimulation() {
        // Replace simulation with live DB status
        try {
            com.example.e_canteenorderingapp.data.AppDatabase db = com.example.e_canteenorderingapp.data.AppDatabase.getInstance(getApplicationContext());
            java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    com.example.e_canteenorderingapp.data.Order order = db.orderDao().getAllOrders()
                            .stream()
                            .filter(o -> o.orderNumber.equals(orderNumber))
                            .findFirst().orElse(null);
                    int status = order != null ? order.status : 0;
                    runOnUiThread(() -> renderStatus(status));
                } catch (Exception e) {
                    // ignore
                }
            });
        } catch (Exception ignored) {}
    }

    private void renderStatus(int status) {
        if (progressTracking == null || tvStatusText == null) return;
        switch (status) {
            case 1:
                progressTracking.setProgress(25);
                tvStatusText.setText("Order confirmed");
                break;
            case 2:
                progressTracking.setProgress(50);
                tvStatusText.setText("Preparing food");
                stepPreparing.setText("✓ Preparing Food");
                stepPreparing.setTextColor(getResources().getColor(R.color.text_dark));
                break;
            case 3:
                progressTracking.setProgress(75);
                tvStatusText.setText("Out for delivery");
                stepOutForDelivery.setText("✓ Out for Delivery");
                stepOutForDelivery.setTextColor(getResources().getColor(R.color.text_dark));
                break;
            case 4:
                progressTracking.setProgress(100);
                tvStatusText.setText("Delivered to Table #" + (tableNumber == null ? "—" : tableNumber));
                stepDelivered.setText("✓ Delivered to Table");
                stepDelivered.setTextColor(getResources().getColor(R.color.text_dark));
                break;
            default:
                progressTracking.setProgress(10);
                tvStatusText.setText("Pending");
        }
    }

    private void setupClickListeners() {
        try {
            Log.d(TAG, "Setting up click listeners");
            
            if (btnViewReceipt != null) {
                btnViewReceipt.setOnClickListener(v -> {
                    Log.d(TAG, "View Receipt button clicked");
                    Toast.makeText(this, "Receipt feature coming soon!", Toast.LENGTH_SHORT).show();
                });
            }
            
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }
            
            btnOrderAgain.setOnClickListener(v -> {
                Log.d(TAG, "Order Again button clicked");
                // Navigate back to student home to order again
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
