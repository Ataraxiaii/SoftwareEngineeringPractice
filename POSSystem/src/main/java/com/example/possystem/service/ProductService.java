package com.example.possystem.service;

import com.example.possystem.model.Product;
import com.example.possystem.util.DBUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ProductService {

    private static final ProductService instance = new ProductService();
    private final ObservableList<Product> products = FXCollections.observableArrayList();

    private ProductService() {
        loadProducts();
    }

    public static ProductService getInstance() {
        return instance;
    }

    public ObservableList<Product> getProducts() {
        return products;
    }

    // load product data
    private void loadProducts() {
        products.clear();
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM product")) {

            while (rs.next()) {
                Product p = new Product(
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getString("status"),
                        rs.getString("image_path")
                );
                products.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // add product
    public void addProduct(Product p) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO product (name, price, stock, status, image_path) VALUES (?,?,?,?,?)")) {
            ps.setString(1, p.getName());
            ps.setDouble(2, p.getPrice());
            ps.setInt(3, p.getStock());
            ps.setString(4, p.getStatus());
            ps.setString(5, p.getImagePath());
            ps.executeUpdate();

            loadProducts(); // update list
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // delete product
    public void removeProduct(Product p) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM product WHERE name = ?")) {
            ps.setString(1, p.getName());
            ps.executeUpdate();

            loadProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // find product by name
    public Product findByName(String name) {
        return products.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    // update product
    public void updateProduct(Product p) {
        String sql =
                "UPDATE product " +
                        "SET price = ?, stock = ?, status = ?, image_path = ? " +
                        "WHERE name = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, p.getPrice());
            ps.setInt(2, p.getStock());

            String newStatus = p.getStock() > 0 ? "Available" : "Sold Out";
            ps.setString(3, newStatus);

            ps.setString(4, p.getImagePath());
            ps.setString(5, p.getName());

            ps.executeUpdate();

            // refresh
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getName().equals(p.getName())) {
                    Product memProduct = products.get(i);
                    memProduct.setStock(p.getStock());
                    memProduct.setStatus(newStatus);
                    break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
