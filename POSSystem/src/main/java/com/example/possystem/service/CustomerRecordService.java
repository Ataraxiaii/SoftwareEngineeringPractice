package com.example.possystem.service;

import com.example.possystem.model.CustomerRecord;
import com.example.possystem.model.OrderItem;
import com.example.possystem.util.DBUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

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

                // load customer order data
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM order_item WHERE record_id=?");
                ps.setInt(1, recordId);
                ResultSet rsItems = ps.executeQuery();

                ObservableList<OrderItem> items = FXCollections.observableArrayList();
                while (rsItems.next()) {
                    String productName = rsItems.getString("product_name");
                    int qty = rsItems.getInt("quantity");
                    double price = rsItems.getDouble("price");
                    items.add(new OrderItem(new com.example.possystem.model.Product(
                            productName, price, 0, "", null
                    ), qty));
                }

                records.add(new CustomerRecord(name, phone, items, total, type));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // add customer record
    public void addRecord(CustomerRecord record) {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO customer_record (customer_name, phone, type, total, create_time) VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, record.getCustomerName());
            ps.setString(2, record.getPhone());
            ps.setString(3, record.getType());
            ps.setDouble(4, record.getTotalAmount());
            ps.setString(5, record.getCreateTime().toString());
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            int recordId = 0;
            if (generatedKeys.next()) recordId = generatedKeys.getInt(1);

            // insert order
            for (OrderItem item : record.getItems()) {
                PreparedStatement psItem = conn.prepareStatement(
                        "INSERT INTO order_item (record_id, product_name, quantity, price) VALUES (?,?,?,?)");
                psItem.setInt(1, recordId);
                psItem.setString(2, item.getProduct().getName());
                psItem.setInt(3, item.getQuantity());
                psItem.setDouble(4, item.getProduct().getPrice());
                psItem.executeUpdate();
            }

            conn.commit();
            records.add(record); // update
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
