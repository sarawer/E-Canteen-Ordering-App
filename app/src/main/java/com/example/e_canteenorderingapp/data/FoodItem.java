package com.example.e_canteenorderingapp.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_items")
public class FoodItem {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String name;

    @NonNull
    public String description;

    // Store drawable resource name or URI string
    @NonNull
    public String imageRef;

    public int priceCents;

    public FoodItem(@NonNull String name,
                    @NonNull String description,
                    @NonNull String imageRef,
                    int priceCents) {
        this.name = name;
        this.description = description;
        this.imageRef = imageRef;
        this.priceCents = priceCents;
    }
}


