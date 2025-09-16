package com.example.e_canteenorderingapp.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart_items", indices = {@Index(value = {"foodId"}, unique = true)})
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long foodId;

    @NonNull
    public String name;

    @NonNull
    public String imageRef;

    public int unitPriceCents;

    public int quantity;

    public CartItem(long foodId,
                    @NonNull String name,
                    @NonNull String imageRef,
                    int unitPriceCents,
                    int quantity) {
        this.foodId = foodId;
        this.name = name;
        this.imageRef = imageRef;
        this.unitPriceCents = unitPriceCents;
        this.quantity = quantity;
    }
}



