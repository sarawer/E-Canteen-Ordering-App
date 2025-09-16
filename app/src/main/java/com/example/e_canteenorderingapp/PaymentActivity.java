package com.example.e_canteenorderingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_canteenorderingapp.data.AppDatabase;
import com.example.e_canteenorderingapp.data.CartItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";
    private TextView tvOrderAmount, tvTaxes, tvTotalAmount, tvFinalTotal, tvDeliveryTime;
    private LinearLayout layoutBkash, layoutNagad, layoutCash;
    private Button btnPayNow;
    private TextView btnBack;
    private EditText etTableNumber;
    
    private String selectedPaymentMethod = ""; // Start with no selection
    private double orderAmount = 0.0;
    private double taxes = 0.0;
    private double totalAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Log.d(TAG, "PaymentActivity onCreate started");
        
        try {
            Log.d(TAG, "Starting view initialization");
            initializeViews();
            Log.d(TAG, "Views initialized successfully");
            
            Log.d(TAG, "Starting cart data loading");
            loadCartData();
            Log.d(TAG, "Cart data loaded successfully");
            
            Log.d(TAG, "Starting click listener setup");
            setupClickListeners();
            Log.d(TAG, "Click listeners setup successfully");
            
            Log.d(TAG, "Starting payment method UI update");
            updatePaymentMethodUI();
            Log.d(TAG, "Payment method UI updated successfully");
            
            Log.d(TAG, "PaymentActivity onCreate completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing payment screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() throws Exception {
        Log.d(TAG, "Initializing individual views");
        
        // Initialize each view with error checking
        tvOrderAmount = findViewById(R.id.tv_order_amount);
        if (tvOrderAmount == null) throw new Exception("tv_order_amount not found");
        Log.d(TAG, "tvOrderAmount initialized");
        
        tvTaxes = findViewById(R.id.tv_taxes);
        if (tvTaxes == null) throw new Exception("tv_taxes not found");
        Log.d(TAG, "tvTaxes initialized");
        
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        if (tvTotalAmount == null) throw new Exception("tv_total_amount not found");
        Log.d(TAG, "tvTotalAmount initialized");
        
        tvFinalTotal = findViewById(R.id.tv_final_total);
        if (tvFinalTotal == null) throw new Exception("tv_final_total not found");
        Log.d(TAG, "tvFinalTotal initialized");
        
        tvDeliveryTime = findViewById(R.id.tv_delivery_time);
        if (tvDeliveryTime == null) throw new Exception("tv_delivery_time not found");
        Log.d(TAG, "tvDeliveryTime initialized");
        
        layoutBkash = findViewById(R.id.layout_bkash);
        if (layoutBkash == null) throw new Exception("layout_bkash not found");
        Log.d(TAG, "layoutBkash initialized");
        
        layoutNagad = findViewById(R.id.layout_nagad);
        if (layoutNagad == null) throw new Exception("layout_nagad not found");
        Log.d(TAG, "layoutNagad initialized");
        
        layoutCash = findViewById(R.id.layout_cash);
        if (layoutCash == null) throw new Exception("layout_cash not found");
        Log.d(TAG, "layoutCash initialized");
        

        
        btnPayNow = findViewById(R.id.btn_pay_now);
        if (btnPayNow == null) throw new Exception("btn_pay_now not found");
        Log.d(TAG, "btnPayNow initialized");
        
        btnBack = findViewById(R.id.btn_back);
        if (btnBack == null) throw new Exception("btn_back not found");
        Log.d(TAG, "btnBack initialized");
        
        etTableNumber = findViewById(R.id.et_table_number);
        if (etTableNumber == null) {
            Log.e(TAG, "CRITICAL: et_table_number view not found in layout!");
            throw new Exception("et_table_number not found");
        }
        Log.d(TAG, "etTableNumber initialized successfully");
        
        // Set default values
        tvOrderAmount.setText("৳0");
        tvTaxes.setText("৳0");
        tvTotalAmount.setText("৳0");
        tvFinalTotal.setText("৳0");
        tvDeliveryTime.setText("15-30mins");
        
        // Keep Pay Now enabled; validation runs on click
        btnPayNow.setEnabled(true);
        btnPayNow.setAlpha(1f);
        
        Log.d(TAG, "All views initialized and default values set");
    }

    private void loadCartData() {
        try {
            Log.d(TAG, "Setting up executor for cart data loading");
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    Log.d(TAG, "Getting database instance");
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    if (db == null) {
                        Log.e(TAG, "Database is null");
                        return;
                    }
                    
                    Log.d(TAG, "Getting cart DAO");
                    if (db.cartDao() == null) {
                        Log.e(TAG, "Cart DAO is null");
                        return;
                    }
                    
                    Log.d(TAG, "Fetching cart items");
                    List<CartItem> cartItems = db.cartDao().getAll();
                    
                    // Calculate totals
                    orderAmount = 0.0;
                    if (cartItems != null) {
                        for (CartItem item : cartItems) {
                            if (item != null) {
                                orderAmount += (item.unitPriceCents * item.quantity) / 100.0;
                            }
                        }
                    }
                    
                    // Calculate taxes (5% of order amount)
                    taxes = orderAmount * 0.05;
                    totalAmount = orderAmount + taxes;
                    
                    Log.d(TAG, "Calculated totals - Order: " + orderAmount + ", Taxes: " + taxes + ", Total: " + totalAmount);
                    
                    runOnUiThread(() -> {
                        try {
                            tvOrderAmount.setText("৳" + String.format("%.2f", orderAmount));
                            tvTaxes.setText("৳" + String.format("%.2f", taxes));
                            tvTotalAmount.setText("৳" + String.format("%.2f", totalAmount));
                            tvFinalTotal.setText("৳" + String.format("%.2f", totalAmount));
                            
                            // Set delivery time based on order size
                            if (totalAmount > 50) {
                                tvDeliveryTime.setText("20-35mins");
                            } else {
                                tvDeliveryTime.setText("15-30mins");
                            }
                            
                            Log.d(TAG, "UI updated successfully");
                        } catch (Exception e) {
                            Log.e(TAG, "Error updating UI: " + e.getMessage(), e);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error loading cart data: " + e.getMessage(), e);
                    runOnUiThread(() -> {
                        Toast.makeText(PaymentActivity.this, "Error loading cart data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
            executor.shutdown();
            Log.d(TAG, "Executor shutdown");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up executor: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            Log.d(TAG, "Setting up click listeners");
            
            btnBack.setOnClickListener(v -> {
                Log.d(TAG, "Back button clicked");
                finish();
            });
            
            layoutBkash.setOnClickListener(v -> {
                Log.d(TAG, "bKash layout clicked");
                selectedPaymentMethod = "bKash";
                updatePaymentMethodUI();
            });
            
            layoutNagad.setOnClickListener(v -> {
                Log.d(TAG, "Nagad layout clicked");
                selectedPaymentMethod = "Nagad";
                updatePaymentMethodUI();
            });
            
            layoutCash.setOnClickListener(v -> {
                Log.d(TAG, "Cash layout clicked");
                selectedPaymentMethod = "Cash";
                updatePaymentMethodUI();
            });
            
            btnPayNow.setOnClickListener(v -> {
                Log.d(TAG, "Pay Now button clicked");
                processPayment();
            });
            
            Log.d(TAG, "All click listeners setup successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
        }
    }

    private void updatePaymentMethodUI() {
        try {
            Log.d(TAG, "Updating payment method UI for: " + selectedPaymentMethod);
            
            if (selectedPaymentMethod.equals("bKash")) {
                layoutBkash.setBackgroundResource(R.drawable.payment_selected_background);
                layoutNagad.setBackgroundResource(R.drawable.payment_unselected_background);
                layoutCash.setBackgroundResource(R.drawable.payment_unselected_background);
            } else if (selectedPaymentMethod.equals("Nagad")) {
                layoutBkash.setBackgroundResource(R.drawable.payment_unselected_background);
                layoutNagad.setBackgroundResource(R.drawable.payment_selected_background);
                layoutCash.setBackgroundResource(R.drawable.payment_unselected_background);
            } else if (selectedPaymentMethod.equals("Cash")) {
                layoutBkash.setBackgroundResource(R.drawable.payment_unselected_background);
                layoutNagad.setBackgroundResource(R.drawable.payment_unselected_background);
                layoutCash.setBackgroundResource(R.drawable.payment_selected_background);
            } else {
                // If no payment method is selected, set all to default light gray
                layoutBkash.setBackgroundResource(R.drawable.payment_unselected_background);
                layoutNagad.setBackgroundResource(R.drawable.payment_unselected_background);
                layoutCash.setBackgroundResource(R.drawable.payment_unselected_background);
            }
            
            Log.d(TAG, "PaymentActivity updated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error updating payment method UI: " + e.getMessage(), e);
        }
    }

    private void processPayment() {
        try {
            Log.d(TAG, "Processing payment for amount: " + totalAmount);
            
            if (totalAmount <= 0) {
                Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedPaymentMethod.isEmpty()) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate table number - SIMPLE AND DIRECT
            String tableNumber = "";
            if (etTableNumber != null) {
                tableNumber = etTableNumber.getText().toString().trim();
                Log.d(TAG, "Table number from EditText: '" + tableNumber + "'");
            } else {
                Log.e(TAG, "EditText is null!");
                Toast.makeText(this, "Error: Table number field not found", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // STRICT VALIDATION - MUST NOT BE EMPTY
            if (tableNumber.isEmpty()) {
                Log.w(TAG, "VALIDATION FAILED: Table number is empty");
                Toast.makeText(this, "Please enter your table number before proceeding", Toast.LENGTH_LONG).show();
                etTableNumber.requestFocus();
                return; // STOP HERE - DO NOT PROCEED
            }
            
            // Additional validation - must be numeric
            try {
                int tableNum = Integer.parseInt(tableNumber);
                if (tableNum <= 0 || tableNum > 999) {
                    Log.w(TAG, "VALIDATION FAILED: Invalid table number range: " + tableNum);
                    Toast.makeText(this, "Please enter a valid table number (1-999)", Toast.LENGTH_LONG).show();
                    etTableNumber.requestFocus();
                    return; // STOP HERE - DO NOT PROCEED
                }
            } catch (NumberFormatException e) {
                Log.w(TAG, "VALIDATION FAILED: Table number not numeric: " + tableNumber);
                Toast.makeText(this, "Please enter a valid table number (numbers only)", Toast.LENGTH_LONG).show();
                etTableNumber.requestFocus();
                return; // STOP HERE - DO NOT PROCEED
            }
            
            Log.d(TAG, "VALIDATION PASSED: Table number is valid: " + tableNumber);

            // Navigate to appropriate payment screen based on selected method
            Intent intent;
            switch (selectedPaymentMethod) {
                case "bKash":
                    intent = new Intent(this, BkashPaymentActivity.class);
                    intent.putExtra("total_amount", totalAmount);
                    intent.putExtra("table_number", tableNumber);
                    startActivity(intent);
                    break;
                    
                case "Nagad":
                    intent = new Intent(this, NagadPaymentActivity.class);
                    intent.putExtra("total_amount", totalAmount);
                    intent.putExtra("table_number", tableNumber);
                    startActivity(intent);
                    break;
                    
                case "Cash":
                    // Navigate to cash confirmation screen (EXACT SAME AS BKASH/NAGAD)
                    intent = new Intent(this, CashConfirmationActivity.class);
                    intent.putExtra("total_amount", totalAmount);
                    intent.putExtra("table_number", tableNumber);
                    
                    // DETAILED DEBUG - CASH FLOW
                    Log.d(TAG, "=== CASH FLOW DEBUG - STEP 1 ===");
                    Log.d(TAG, "tableNumber variable: '" + tableNumber + "'");
                    Log.d(TAG, "tableNumber length: " + (tableNumber != null ? tableNumber.length() : "null"));
                    Log.d(TAG, "tableNumber class: " + (tableNumber != null ? tableNumber.getClass().getSimpleName() : "null"));
                    Log.d(TAG, "Putting extra 'table_number' = '" + tableNumber + "'");
                    Log.d(TAG, "================================");
                    
                    startActivity(intent);
                    break;
                    
                default:
                    Toast.makeText(this, "Invalid payment method selected", Toast.LENGTH_SHORT).show();
                    break;
            }
            
            Log.d(TAG, "Navigation to " + selectedPaymentMethod + " payment screen completed");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in processPayment: " + e.getMessage(), e);
            Toast.makeText(this, "Error processing payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearCart() {
        try {
            Log.d(TAG, "Clearing cart");
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    if (db != null && db.cartDao() != null) {
                        db.cartDao().clear();
                        Log.d(TAG, "Cart cleared successfully");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error clearing cart: " + e.getMessage(), e);
                }
            });
            executor.shutdown();
        } catch (Exception e) {
            Log.e(TAG, "Error setting up cart clearing: " + e.getMessage(), e);
        }
    }
}
