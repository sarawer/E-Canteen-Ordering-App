package com.example.e_canteenorderingapp.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_canteenorderingapp.R;
import com.example.e_canteenorderingapp.util.FormatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class SalesItemsAdapter extends RecyclerView.Adapter<SalesItemsAdapter.RowVH> {

    static class Row {
        String name; int qty; int revenueCents;
        Row(String name, int qty, int revenueCents) { this.name = name; this.qty = qty; this.revenueCents = revenueCents; }
    }

    private final List<Row> rows = new ArrayList<>();

    void submit(List<Row> data) {
        rows.clear();
        if (data != null) rows.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public RowVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales_report_row, parent, false);
        return new RowVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RowVH holder, int position) {
        Row r = rows.get(position);
        holder.name.setText(r.name);
        holder.qty.setText("x" + r.qty);
        holder.rev.setText(FormatUtils.formatBdt(r.revenueCents));
    }

    @Override public int getItemCount() { return rows.size(); }

    static class RowVH extends RecyclerView.ViewHolder {
        TextView name, qty, rev;
        RowVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_name);
            qty = itemView.findViewById(R.id.tv_item_qty);
            rev = itemView.findViewById(R.id.tv_item_rev);
        }
    }
}


