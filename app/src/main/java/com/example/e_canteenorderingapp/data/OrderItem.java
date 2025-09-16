package com.example.e_canteenorderingapp.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_items",
        foreignKeys = @ForeignKey(entity = Order.class,
                parentColumns = "orderNumber",
                childColumns = "orderNumber",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("orderNumber")})
public class OrderItem {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String orderNumber;

    public long foodId;

    public String name;

    public int unitPriceCents;

    public int quantity;

    public OrderItem(String orderNumber,
                     long foodId,
                     String name,
                     int unitPriceCents,
                     int quantity) {
        this.orderNumber = orderNumber;
        this.foodId = foodId;
        this.name = name;
        this.unitPriceCents = unitPriceCents;
        this.quantity = quantity;
    }
}


