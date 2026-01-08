package com.example.possystem.controller;

import com.example.possystem.model.CustomerRecord;
import com.example.possystem.model.Order;
import com.example.possystem.service.CustomerRecordService;
import com.example.possystem.util.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import com.example.possystem.model.OrderItem;
import com.example.possystem.model.Product;
import com.example.possystem.service.ProductService;

public class PaymentController {

    @FXML private Label totalLabel;
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField payField;

    private Order order;

    // Pass the order from the cashier interface
    public void setOrder(Order order) {
        this.order = order;
        totalLabel.setText("Total: $" + order.getTotalAmount());
    }

    public void complete() {

        if (nameField.getText().isEmpty() || phoneField.getText().isEmpty()) {
            alert("Customer information is required");
            return;
        }

        double paid;
        try {
            paid = Double.parseDouble(payField.getText());
        } catch (Exception e) {
            alert("Invalid payment amount");
            return;
        }

        if (paid < order.getTotalAmount()) {
            alert("Paid amount is insufficient");
            return;
        }

        // Fill in order information
        order.setCustomerName(nameField.getText());
        order.setPhone(phoneField.getText());
        order.setPaidAmount(paid);

        // save to Customer databases
        try {
            // change order to recorder
            CustomerRecord record = order.toCustomerRecord();

            // store to the database
            CustomerRecordService.getInstance().addRecord(record);
            order.setId(record.getId());

            // refresh the stock
            ProductService productService = ProductService.getInstance();
            for (OrderItem item : order.getItems()) {
                productService.updateProduct(item.getProduct());
            }

        } catch (Exception e) {
            e.printStackTrace();
            alert("Failed to complete transaction");
            return;
        }

        // Receipt screen
        SceneSwitcher.switchScene("/com/example/possystem/receipt.fxml", controller -> {
            ((ReceiptController) controller).setOrder(order);
        });
    }

    public void cancel() {
        if (order != null && order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Product p = item.getProduct();
                // restore stock
                p.setStock(p.getStock() + item.getQuantity());
            }
        }

        SceneSwitcher.switchScene("/com/example/possystem/sale.fxml");
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.showAndWait();
    }
}
