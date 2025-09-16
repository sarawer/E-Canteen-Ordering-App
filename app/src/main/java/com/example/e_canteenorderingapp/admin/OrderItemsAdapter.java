package com.example.e_canteenorderingapp.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_canteenorderingapp.R;
import com.example.e_canteenorderingapp.data.OrderItem;

import java.util.ArrayList;
import java.util.List;

class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ItemVH> {
    private final List<OrderItem> items = new ArrayList<>();

    public void submit(List<OrderItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order_detail_line, parent, false);
        return new ItemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemVH holder, int position) {
        OrderItem it = items.get(position);
        holder.name.setText(it.name);
        holder.qty.setText("x" + it.quantity);
        holder.price.setText("৳" + (it.unitPriceCents / 100));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ItemVH extends RecyclerView.ViewHolder {
        TextView name, qty, price;
        ItemVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_name);
            qty = itemView.findViewById(R.id.tv_item_qty);
            price = itemView.findViewById(R.id.tv_item_price);
        }
    }
}


