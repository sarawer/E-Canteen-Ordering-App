package com.example.e_canteenorderingapp.student;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_canteenorderingapp.R;
import com.example.e_canteenorderingapp.data.CartItem;

import java.util.ArrayList;
import java.util.List;

class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartVH> {

    interface CartListener {
        void onIncrement(CartItem item);
        void onDecrement(CartItem item);
        void onRemove(CartItem item);
    }

    private final List<CartItem> items = new ArrayList<>();
    private final CartListener listener;

    CartAdapter(CartListener listener) { this.listener = listener; }

    void submit(List<CartItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public CartVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartVH h, int position) {
        CartItem it = items.get(position);
        Context ctx = h.itemView.getContext();
        
        // Load image
        if (it.imageRef != null && (it.imageRef.startsWith("content://") || it.imageRef.startsWith("file://"))) {
            com.bumptech.glide.Glide.with(ctx).load(android.net.Uri.parse(it.imageRef)).centerCrop().placeholder(R.drawable.burger_combo).into(h.iv);
        } else {
            int resId = ctx.getResources().getIdentifier(it.imageRef, "drawable", ctx.getPackageName());
            if (resId == 0) resId = R.drawable.burger_combo;
            h.iv.setImageResource(resId);
        }
        
        h.name.setText(it.name);
        h.price.setText("৳" + (it.unitPriceCents / 100));
        h.rating.setText("4.5"); // Default rating for now
        h.qty.setText(String.valueOf(it.quantity));
        
        h.btnInc.setOnClickListener(v -> listener.onIncrement(it));
        h.btnDec.setOnClickListener(v -> listener.onDecrement(it));
        h.btnDelete.setOnClickListener(v -> listener.onRemove(it));
    }

    @Override public int getItemCount() { return items.size(); }

    static class CartVH extends RecyclerView.ViewHolder {
        ImageView iv; 
        TextView name; 
        TextView price; 
        TextView rating;
        TextView qty; 
        TextView btnInc; 
        TextView btnDec;
        ImageView btnDelete;
        
        CartVH(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv_food);
            name = itemView.findViewById(R.id.tv_name);
            price = itemView.findViewById(R.id.tv_price);
            rating = itemView.findViewById(R.id.tv_rating);
            qty = itemView.findViewById(R.id.tv_qty);
            btnInc = itemView.findViewById(R.id.btn_inc);
            btnDec = itemView.findViewById(R.id.btn_dec);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}


