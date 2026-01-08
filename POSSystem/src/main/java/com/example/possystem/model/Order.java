package com.example.possystem.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private int id;

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

    public com.example.possystem.model.CustomerRecord toCustomerRecord() {
        return new com.example.possystem.model.CustomerRecord(
                this.getCustomerName(),
                this.getPhone(),
                this.getItems(),
                this.getTotalAmount(),
                "Shopping"
        );
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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getCreateTime() { return createTime; }
}
