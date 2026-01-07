package com.example.possystem.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerRecord {

    private String customerName;
    private String phone;
    private List<OrderItem> items;
    private double totalAmount;
    private String type; // "Shopping" or "Returning"
    private LocalDateTime createTime;

    public CustomerRecord(String customerName, String phone, List<OrderItem> items,
                          double totalAmount, String type) {
        this.customerName = customerName;
        this.phone = phone;
        this.items = items;
        this.totalAmount = totalAmount;
        this.type = type;
        this.createTime = LocalDateTime.now();
    }

    public String getCustomerName() { return customerName; }
    public String getPhone() { return phone; }
    public List<OrderItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public String getType() { return type; }
    public LocalDateTime getCreateTime() { return createTime; }

    // Product list string, for example: Chips*4, Cola*2
    public String getItemsSummary() {
        return items.stream()
                .map(i -> i.getProduct().getName() + "*" + i.getQuantity())
                .collect(Collectors.joining(", "));
    }
}