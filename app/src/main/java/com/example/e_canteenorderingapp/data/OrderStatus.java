package com.example.e_canteenorderingapp.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class OrderStatus {
    @PrimaryKey
    @NonNull
    public String orderNumber;

    // 1=confirmed, 2=preparing, 3=out_for_delivery, 4=delivered
    public int status;

    public String tableNumber;

    public long updatedAt;

    public OrderStatus(@NonNull String orderNumber, int status, String tableNumber, long updatedAt) {
        this.orderNumber = orderNumber;
        this.status = status;
        this.tableNumber = tableNumber;
        this.updatedAt = updatedAt;
    }
}


