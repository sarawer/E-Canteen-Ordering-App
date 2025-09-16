package com.example.e_canteenorderingapp.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FoodDao {
    @Query("SELECT * FROM food_items ORDER BY id DESC")
    List<FoodItem> getAll();

    @Insert
    long insert(FoodItem item);

    @Delete
    int delete(FoodItem item);
}


