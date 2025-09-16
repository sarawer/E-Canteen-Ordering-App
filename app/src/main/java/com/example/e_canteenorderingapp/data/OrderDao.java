package com.example.e_canteenorderingapp.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertOrder(Order order);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItems(List<OrderItem> items);

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    List<Order> getAllOrders();

    @Query("SELECT * FROM order_items WHERE orderNumber = :orderNumber")
    List<OrderItem> getItemsForOrder(String orderNumber);

    @Query("UPDATE orders SET status = :status WHERE orderNumber = :orderNumber")
    int updateStatus(String orderNumber, int status);

    @Query("SELECT COUNT(*) FROM orders")
    int getTotalOrders();

    @Query("SELECT SUM(totalCents) FROM orders")
    Integer getTotalRevenueCents();

    @Query("SELECT COUNT(*) FROM orders WHERE createdAt BETWEEN :start AND :end")
    int getTotalOrdersBetween(long start, long end);

    @Query("SELECT COALESCE(SUM(totalCents),0) FROM orders WHERE createdAt BETWEEN :start AND :end")
    int getRevenueBetween(long start, long end);
}


