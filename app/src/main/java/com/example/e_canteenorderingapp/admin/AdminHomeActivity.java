package com.example.e_canteenorderingapp.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.e_canteenorderingapp.R;
import androidx.appcompat.app.AlertDialog;
import com.example.e_canteenorderingapp.WelcomeActivity;

public class AdminHomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Button btnManageMenu = findViewById(R.id.btn_manage_menu);
        Button btnOrders = findViewById(R.id.btn_orders);
        Button btnReports = findViewById(R.id.btn_reports);
        Button btnLogout = findViewById(R.id.btn_admin_logout);
        Button btnAi = findViewById(R.id.btn_ai_suggestion);

        btnManageMenu.setOnClickListener(v ->
                startActivity(new Intent(this, AdminDashboardActivity.class))
        );

        btnOrders.setOnClickListener(v -> startActivity(new Intent(this, AdminOrdersActivity.class)));

        btnReports.setOnClickListener(v -> startActivity(new Intent(this, AdminSalesReportActivity.class)));

        if (btnAi != null) {
            btnAi.setOnClickListener(v -> startActivity(new Intent(this, AdminAiSuggestionActivity.class)));
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("users", MODE_PRIVATE);
                sp.edit().remove("session_user_id").remove("session_role").apply();
                Intent i = new Intent(AdminHomeActivity.this, WelcomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit app?")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Exit", (dialog, which) -> finishAffinity())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}


