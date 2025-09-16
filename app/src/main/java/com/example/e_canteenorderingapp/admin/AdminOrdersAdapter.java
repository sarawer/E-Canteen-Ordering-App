package com.example.e_canteenorderingapp.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.content.Intent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_canteenorderingapp.R;
import com.example.e_canteenorderingapp.data.OrderStatus;

import java.util.ArrayList;
import java.util.List;

public class AdminOrdersAdapter extends RecyclerView.Adapter<AdminOrdersAdapter.OrderVH> {

    interface OnOrderActionListener {
        void onStatusChanged(OrderStatus order, int newStatus);
    }

    private final List<OrderStatus> items = new ArrayList<>();
    private final OnOrderActionListener listener;

    public AdminOrdersAdapter(OnOrderActionListener listener) {
        this.listener = listener;
    }

    public void submit(List<OrderStatus> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order, parent, false);
        return new OrderVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderVH holder, int position) {
        OrderStatus os = items.get(position);
        holder.tvOrderNumber.setText(os.orderNumber);
        // Status label
        boolean complete = os.status >= 3;
        holder.tvStatus.setText(complete ? "Complete" : "Pending");
        holder.tvStatus.setTextColor(holder.itemView.getResources().getColor(R.color.green));

        // Open detail on click
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), AdminOrderDetailActivity.class);
            i.putExtra("order_number", os.orderNumber);
            v.getContext().startActivity(i);
        });
    }

    private String statusLabel(int status) {
        switch (status) {
            case 1: return "Confirmed";
            case 2: return "Preparing";
            case 3: return "Out for Delivery";
            case 4: return "Delivered";
            default: return "Pending";
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class OrderVH extends RecyclerView.ViewHolder {
        TextView tvOrderNumber, tvStatus;
        OrderVH(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tv_order_number);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}


