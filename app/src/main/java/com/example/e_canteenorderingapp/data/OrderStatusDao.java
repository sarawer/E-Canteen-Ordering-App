package com.example.e_canteenorderingapp.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface OrderStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long upsert(OrderStatus status);

    @Update
    int update(OrderStatus status);

    @Query("SELECT * FROM OrderStatus WHERE orderNumber = :orderNumber LIMIT 1")
    OrderStatus getByOrderNumber(String orderNumber);

    @Query("UPDATE OrderStatus SET status = :status, updatedAt = :updatedAt WHERE orderNumber = :orderNumber")
    int updateStatus(String orderNumber, int status, long updatedAt);
}


