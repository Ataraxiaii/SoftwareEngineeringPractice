package com.example.possystem.controller;

import com.example.possystem.util.SceneSwitcher;

public class MainController {

    public void openProduct() {
        SceneSwitcher.switchScene("/com/example/possystem/product.fxml");
        System.out.println("Enter the product management interface");
    }

    public void openSale() {
        System.out.println("Enter the sale interface");
    }

    public void openReturn() {
        System.out.println("Enter the return interface");
    }

    public void openCustomer() {
        System.out.println("Enter the customer list interface");
    }
}

