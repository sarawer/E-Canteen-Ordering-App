package com.example.e_canteenorderingapp.student;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_canteenorderingapp.R;
import com.example.e_canteenorderingapp.data.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodVH> {

    public interface OnFoodAction {
        void onAddToCart(FoodItem item);
    }

    private final List<FoodItem> items = new ArrayList<>();
    private final List<FoodItem> full = new ArrayList<>();
    private OnFoodAction listener;

    public void setListener(OnFoodAction l) { this.listener = l; }

    public void submit(List<FoodItem> data) {
        items.clear();
        full.clear();
        if (data != null) {
            items.addAll(data);
            full.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void filterByName(String query) {
        items.clear();
        if (query == null || query.trim().isEmpty()) {
            items.addAll(full);
        } else {
            String q = query.toLowerCase();
            for (FoodItem f : full) {
                if (f.name.toLowerCase().contains(q)) items.add(f);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodVH holder, int position) {
        FoodItem item = items.get(position);
        holder.tvName.setText(item.name);
        holder.tvDesc.setText(item.description);
        holder.tvPrice.setText("৳ " + (item.priceCents / 100));

        // Basic image mapping: if imageRef equals "burger_combo" show that drawable
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

        holder.btnOrder.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCart(item);
        });

        // Click on card opens product details
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(ctx, com.example.e_canteenorderingapp.ProductDetailsActivity.class);
            intent.putExtra("food_id", item.id);
            intent.putExtra("food_name", item.name);
            intent.putExtra("food_desc", item.description);
            intent.putExtra("food_image", item.imageRef);
            intent.putExtra("food_price", item.priceCents);
            ctx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FoodVH extends RecyclerView.ViewHolder {
        ImageView ivFood;
        TextView tvName;
        TextView tvDesc;
        TextView tvPrice;
        View btnOrder;

        FoodVH(@NonNull View itemView) {
            super(itemView);
            ivFood = itemView.findViewById(R.id.iv_food);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnOrder = itemView.findViewById(R.id.btn_order);
        }
    }
}


