package com.example.possystem.service;

import com.example.possystem.model.CustomerRecord;
import com.example.possystem.model.OrderItem;
import com.example.possystem.model.Product;
import com.example.possystem.util.DBUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomerRecordService {

    private static final CustomerRecordService instance = new CustomerRecordService();
    private final ObservableList<CustomerRecord> records = FXCollections.observableArrayList();

    private CustomerRecordService() {
        loadRecords();
    }

    public static CustomerRecordService getInstance() {
        return instance;
    }

    public ObservableList<CustomerRecord> getRecords() {
        return records;
    }

    private void loadRecords() {
        records.clear();
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customer_record")) {

            while (rs.next()) {
                int recordId = rs.getInt("id");
                String name = rs.getString("customer_name");
                String phone = rs.getString("phone");
                String type = rs.getString("type");
                double total = rs.getDouble("total");
                LocalDateTime createTime = LocalDateTime.parse(rs.getString("create_time"));

                // load customer order data
                List<OrderItem> items = new ArrayList<>();
                try (PreparedStatement psItem = conn.prepareStatement("SELECT * FROM order_item WHERE record_id=?")) {
                    psItem.setInt(1, recordId);
                    try (ResultSet rsItems = psItem.executeQuery()) {
                        while (rsItems.next()) {
                            String productName = rsItems.getString("product_name");
                            int qty = rsItems.getInt("quantity");
                            double price = rsItems.getDouble("price");

                            Product realProduct = ProductService.getInstance().findByName(productName);
                            if (realProduct == null) {
                                realProduct = new Product(productName, price, 0, "Deleted", null);
                            }
                            items.add(new OrderItem(realProduct, qty));
                        }
                    }
                }

                CustomerRecord record = new CustomerRecord(recordId, name, phone, items, total, type, createTime);
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // add customer record
    public void addRecord(CustomerRecord record) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            // insert customer
            String sqlRecord = "INSERT INTO customer_record (customer_name, phone, type, total, create_time) VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlRecord, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, record.getCustomerName());
                ps.setString(2, record.getPhone());
                ps.setString(3, record.getType());
                ps.setDouble(4, record.getTotalAmount());
                ps.setString(5, record.getCreateTime().toString());
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    int id = keys.getInt(1);
                    record.setId(id);
                }
            }

            // insert order
            String sqlItem = "INSERT INTO order_item (record_id, product_name, quantity, price) VALUES (?,?,?,?)";
            try (PreparedStatement psItem = conn.prepareStatement(sqlItem)) {
                for (OrderItem item : record.getItems()) {
                    psItem.setInt(1, record.getId());
                    psItem.setString(2, item.getProduct().getName());
                    psItem.setInt(3, item.getQuantity());
                    psItem.setDouble(4, item.getProduct().getPrice());
                    psItem.addBatch();
                }
                psItem.executeBatch();
            }

            conn.commit();
            records.add(0, record); // update
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // delete customer record
    public void removeRecord(CustomerRecord record) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            // delete order_items first
            try (PreparedStatement psItem = conn.prepareStatement(
                    "DELETE FROM order_item WHERE record_id=?")) {
                psItem.setInt(1, record.getId());
                psItem.executeUpdate();
            }

            // delete customer_record
            try (PreparedStatement psRecord = conn.prepareStatement(
                    "DELETE FROM customer_record WHERE id=?")) {
                psRecord.setInt(1, record.getId());
                psRecord.executeUpdate();
            }

            conn.commit();
            records.remove(record);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // find record by invoice id and type is shopping
    public CustomerRecord findById(int id) {
        return records.stream()
                .filter(r -> r.getId() == id && "Shopping".equals(r.getType()))
                .findFirst()
                .orElse(null);
    }
}
