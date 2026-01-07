package com.example.possystem.model;

public class Product {

    private String name;
    private double price;
    private int stock;
    private String status; // Available / Sold Out
    private String imagePath;

    public Product(String name, double price, int stock, String status, String imagePath) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.imagePath = imagePath;
    }

    // ===== Getter & Setter =====
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
