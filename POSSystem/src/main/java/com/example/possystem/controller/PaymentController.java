package com.example.possystem.controller;

import com.example.possystem.model.Order;
import com.example.possystem.util.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

        // Receipt screen
        SceneSwitcher.switchScene("/com/example/possystem/receipt.fxml");
    }

    public void cancel() {
        SceneSwitcher.switchScene("/com/example/possystem/sale.fxml");
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.showAndWait();
    }
}
