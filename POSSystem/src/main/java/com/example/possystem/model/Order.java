package com.example.possystem.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private String customerName;
    private String phone;
    private List<OrderItem> items;
    private double totalAmount;
    private double paidAmount;
    private LocalDateTime createTime;

    public Order(List<OrderItem> items, double totalAmount) {
        this.items = items;
        this.totalAmount = totalAmount;
        this.createTime = LocalDateTime.now();
    }

    // ===== getter & setter =====
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<OrderItem> getItems() { return items; }

    public double getTotalAmount() { return totalAmount; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    public LocalDateTime getCreateTime() { return createTime; }
}
