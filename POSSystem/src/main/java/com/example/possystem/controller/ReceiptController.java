package com.example.possystem.controller;

import com.example.possystem.model.CustomerRecord;
import com.example.possystem.model.CustomerRecordManager;
import com.example.possystem.model.Order;
import com.example.possystem.model.OrderItem;
import com.example.possystem.service.CustomerRecordService;
import com.example.possystem.util.SceneSwitcher;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ReceiptController {

    @FXML private Label customerLabel;
    @FXML private Label phoneLabel;
    @FXML private TableView<OrderItem> receiptTable;
    @FXML private TableColumn<OrderItem, String> nameCol;
    @FXML private TableColumn<OrderItem, Double> priceCol;
    @FXML private TableColumn<OrderItem, Integer> qtyCol;
    @FXML private TableColumn<OrderItem, Double> subtotalCol;

    @FXML private Label totalLabel;
    @FXML private Label paidLabel;
    @FXML private Label changeLabel;

    private Order order;
    private ObservableList<OrderItem> itemList = FXCollections.observableArrayList();

    // Pass the order from the payment interface
    public void setOrder(Order order) {
        this.order = order;

        customerLabel.setText("Customer: " + order.getCustomerName());
        phoneLabel.setText("Phone: " + order.getPhone());

        itemList.addAll(order.getItems());
        receiptTable.setItems(itemList);

        nameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProduct().getName()));
        priceCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getProduct().getPrice()).asObject());
        qtyCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        subtotalCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());

        // Total / Pay / Change
        totalLabel.setText("Total: $" + order.getTotalAmount());
        paidLabel.setText("Paid: $" + order.getPaidAmount());
        double change = order.getPaidAmount() - order.getTotalAmount();
        changeLabel.setText("Change: $" + change);
    }

    // Go back to main page
    public void finish() {
        // record shopping
        try {
            CustomerRecord record = new CustomerRecord(
                    order.getCustomerName(),
                    order.getPhone(),
                    order.getItems(),
                    order.getTotalAmount(),
                    "Shopping"
            );

            // Back to main pages
            SceneSwitcher.switchScene("/com/example/possystem/main.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
