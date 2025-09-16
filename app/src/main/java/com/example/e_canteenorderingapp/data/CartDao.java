package com.example.e_canteenorderingapp.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    List<CartItem> getAll();

    @Query("SELECT SUM(unitPriceCents * quantity) FROM cart_items")
    Integer getTotalCents();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CartItem item);

    @Update
    int update(CartItem item);

    @Delete
    int delete(CartItem item);

    @Query("DELETE FROM cart_items")
    void clear();
}


