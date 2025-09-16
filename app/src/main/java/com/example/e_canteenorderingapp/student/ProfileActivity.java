package com.example.e_canteenorderingapp.student;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_canteenorderingapp.R;
import com.example.e_canteenorderingapp.WelcomeActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView tvName = findViewById(R.id.tv_name);
        TextView tvId = findViewById(R.id.tv_id);
        Button btnLogout = findViewById(R.id.btn_logout);
        Button btnTrack = findViewById(R.id.btn_track_order);

        SharedPreferences users = getSharedPreferences("users", MODE_PRIVATE);
        String userId = users.getString("session_user_id", "");
        String name = users.getString("user_" + userId + "_name", "");

        tvName.setText("Name: " + name);
        tvId.setText("ID: " + userId);

        btnLogout.setOnClickListener(v -> {
            users.edit().remove("session_user_id").remove("session_role").apply();
            Intent i = new Intent(this, WelcomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });

        btnTrack.setOnClickListener(v -> {
            Intent i = new Intent(this, com.example.e_canteenorderingapp.OrderTrackingActivity.class);
            // For demo, pass last order info if stored; else open with placeholders
            i.putExtra("order_number", "EC-2024-001");
            i.putExtra("total_amount", 0.0);
            i.putExtra("payment_method", "");
            i.putExtra("table_number", "");
            startActivity(i);
        });

        // Bottom nav wiring + highlight
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            startActivity(new Intent(this, StudentHomeActivity.class));
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.nav_cart).setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
            overridePendingTransition(0, 0);
        });
        findViewById(R.id.nav_profile).startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.nav_pulse));
    }
}


