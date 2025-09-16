package com.example.e_canteenorderingapp.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders")
public class Order {
    @PrimaryKey
    @NonNull
    public String orderNumber;

    public long createdAt;

    public String tableNumber;

    public String paymentMethod;

    public int totalCents;

    public String studentName; // optional

    public int estimatedMinutes; // optional

    // 0=pending, 1=confirmed, 2=preparing, 3=out_for_delivery, 4=delivered
    public int status;

    public Order(@NonNull String orderNumber,
                 long createdAt,
                 String tableNumber,
                 String paymentMethod,
                 int totalCents,
                 String studentName,
                 int estimatedMinutes,
                 int status) {
        this.orderNumber = orderNumber;
        this.createdAt = createdAt;
        this.tableNumber = tableNumber;
        this.paymentMethod = paymentMethod;
        this.totalCents = totalCents;
        this.studentName = studentName;
        this.estimatedMinutes = estimatedMinutes;
        this.status = status;
    }
}


