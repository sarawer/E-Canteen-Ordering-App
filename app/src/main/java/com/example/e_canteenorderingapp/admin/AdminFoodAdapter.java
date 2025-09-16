package com.example.e_canteenorderingapp.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_canteenorderingapp.R;
import com.example.e_canteenorderingapp.data.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class AdminFoodAdapter extends RecyclerView.Adapter<AdminFoodAdapter.AdminVH> {

    interface OnAdminAction {
        void onDelete(FoodItem item);
    }

    private final List<FoodItem> items = new ArrayList<>();
    private final OnAdminAction listener;

    public AdminFoodAdapter(OnAdminAction listener) {
        this.listener = listener;
    }

    public void submit(List<FoodItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_admin, parent, false);
        return new AdminVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminVH holder, int position) {
        FoodItem item = items.get(position);
        Context ctx = holder.itemView.getContext();
        if (item.imageRef != null && (item.imageRef.startsWith("content://") || item.imageRef.startsWith("file://"))) {
            com.bumptech.glide.Glide.with(ctx)
                    .load(android.net.Uri.parse(item.imageRef))
                    .placeholder(R.drawable.burger_combo)
                    .centerCrop()
                    .into(holder.ivFood);
        } else {
            int resId = ctx.getResources().getIdentifier(item.imageRef, "drawable", ctx.getPackageName());
            if (resId == 0) resId = R.drawable.burger_combo;
            holder.ivFood.setImageResource(resId);
        }
        holder.tvName.setText(item.name);
        holder.tvDesc.setText(item.description);
        holder.tvPrice.setText("৳ " + (item.priceCents / 100));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class AdminVH extends RecyclerView.ViewHolder {
        ImageView ivFood;
        TextView tvName, tvDesc, tvPrice;
        ImageButton btnDelete;

        AdminVH(@NonNull View itemView) {
            super(itemView);
            ivFood = itemView.findViewById(R.id.iv_food);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}


