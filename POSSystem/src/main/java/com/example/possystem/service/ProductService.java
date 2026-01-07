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

    // update product
    public void updateProduct(Product oldP, Product newP) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE product SET name=?, price=?, stock=?, status=?, image_path=? WHERE name=?")) {

            ps.setString(1, newP.getName());
            ps.setDouble(2, newP.getPrice());
            ps.setInt(3, newP.getStock());
            ps.setString(4, newP.getStatus());
            ps.setString(5, newP.getImagePath());
            ps.setString(6, oldP.getName());
            ps.executeUpdate();

            loadProducts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
