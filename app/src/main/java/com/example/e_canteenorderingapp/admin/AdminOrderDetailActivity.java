package com.example.e_canteenorderingapp.admin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_canteenorderingapp.R;
import com.example.e_canteenorderingapp.data.AppDatabase;
import com.example.e_canteenorderingapp.data.Order;
import com.example.e_canteenorderingapp.data.OrderItem;

import java.util.List;

public class AdminOrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderNumber, tvStudentName, tvTable, tvEstimate;
    private CheckBox cbConfirmed, cbPreparing, cbOutForDelivery;
    private RecyclerView rvItems;

    private String orderNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_detail);

        orderNumber = getIntent().getStringExtra("order_number");

        tvOrderNumber = findViewById(R.id.tv_order_number);
        tvStudentName = findViewById(R.id.tv_student_name);
        tvTable = findViewById(R.id.tv_table_number);
        tvEstimate = findViewById(R.id.tv_estimate_time);
        rvItems = findViewById(R.id.rv_items);
        cbConfirmed = findViewById(R.id.cb_confirmed);
        cbPreparing = findViewById(R.id.cb_preparing);
        cbOutForDelivery = findViewById(R.id.cb_out_for_delivery);

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(new OrderItemsAdapter());

        loadDetails();
    }

    private void loadDetails() {
        new AsyncTask<Void, Void, Object[]>() {
            @Override
            protected Object[] doInBackground(Void... voids) {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                Order order = null;
                List<OrderItem> items = null;
                try {
                    for (Order o : db.orderDao().getAllOrders()) {
                        if (o.orderNumber.equals(orderNumber)) { order = o; break; }
                    }
                } catch (Exception ignored) {}
                try { items = db.orderDao().getItemsForOrder(orderNumber); } catch (Exception ignored) {}
                return new Object[]{ order, items };
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void onPostExecute(Object[] data) {
                Order order = (Order) data[0];
                List<OrderItem> items = (List<OrderItem>) data[1];
                if (order == null) {
                    Toast.makeText(AdminOrderDetailActivity.this, "Order not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                tvOrderNumber.setText(order.orderNumber);
                String tableText = (order.tableNumber == null || order.tableNumber.isEmpty()) ? "—" : order.tableNumber;
                String studentText = (order.studentName == null || order.studentName.isEmpty()) ? "—" : order.studentName;
                tvStudentName.setText("Student name: " + studentText);
                tvTable.setText("Table number: " + tableText);
                String estimateText = order.estimatedMinutes > 0 ? ("~" + order.estimatedMinutes + " min") : "~20 min";
                tvEstimate.setText("Estimated delivery time: " + estimateText);

                ((OrderItemsAdapter) rvItems.getAdapter()).submit(items);

                cbConfirmed.setChecked(order.status >= 1);
                cbPreparing.setChecked(order.status >= 2);
                cbOutForDelivery.setChecked(order.status >= 3);

                cbConfirmed.setOnCheckedChangeListener((b, c) -> { if (c) updateStatus(1); });
                cbPreparing.setOnCheckedChangeListener((b, c) -> { if (c) updateStatus(2); });
                cbOutForDelivery.setOnCheckedChangeListener((b, c) -> { if (c) updateStatus(3); });
            }
        }.execute();
    }


    private void updateStatus(int status) {
        new AsyncTask<Integer, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Integer... params) {
                try {
                    AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                    int s = params[0];
                    int a = db.orderDao().updateStatus(orderNumber, s);
                    int b = db.orderStatusDao().updateStatus(orderNumber, s, System.currentTimeMillis());
                    return a > 0 || b > 0;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean ok) {
                if (!ok) Toast.makeText(AdminOrderDetailActivity.this, "Status update failed", Toast.LENGTH_SHORT).show();
                // no visual refresh needed; checkboxes reflect persisted state on reopen
            }
        }.execute(status);
    }
}


