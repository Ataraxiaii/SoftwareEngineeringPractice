package com.example.possystem.controller;

import com.example.possystem.model.CustomerRecord;
import com.example.possystem.model.OrderItem;
import com.example.possystem.model.Product;
import com.example.possystem.service.CustomerRecordService;
import com.example.possystem.service.ProductService;
import com.example.possystem.util.SceneSwitcher;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReturnController {

    @FXML private TextField searchField;
    @FXML private TableView<OrderItem> originalItemsTable;
    @FXML private TableColumn<OrderItem, String> originalNameCol;
    @FXML private TableColumn<OrderItem, Double> originalPriceCol;
    @FXML private TableColumn<OrderItem, Integer> originalQtyCol;

    @FXML private TextArea historyReturnArea;

    @FXML private ComboBox<String> productComboBox;
    @FXML private TextField inputReturnQty;
    @FXML private TableView<OrderItem> returnItemsTable;
    @FXML private TableColumn<OrderItem, String> returnNameCol;
    @FXML private TableColumn<OrderItem, Integer> returnQtyCol;
    @FXML private TableColumn<OrderItem, Double> returnSubtotalCol;

    @FXML private TextField returnReasonField;
    @FXML private Label totalRefundLabel;

    @FXML private Label customerNameLabel;
    @FXML private Label customerPhoneLabel;
    @FXML private Label purchaseTimeLabel;

    private CustomerRecord originalRecord;
    private final ObservableList<OrderItem> currentReturnList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // original product table
        originalNameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProduct().getName()));
        originalPriceCol.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getProduct().getPrice()).asObject());
        originalQtyCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getQuantity()).asObject());

        // return product history
        returnNameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProduct().getName()));
        returnQtyCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getQuantity()).asObject());
        returnSubtotalCol.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getSubtotal()).asObject());

        returnItemsTable.setItems(currentReturnList);
    }

    // search by the invoice id
    @FXML
    public void searchRecords() {
        String idText = searchField.getText().trim();
        if (idText.isEmpty()) return;

        try {
            int id = Integer.parseInt(idText);
            CustomerRecord record = CustomerRecordService.getInstance().findById(id);

            if (record != null) {
                this.originalRecord = record;
                // show name, phone and time
                customerNameLabel.setText(record.getCustomerName());
                customerPhoneLabel.setText(record.getPhone());
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                purchaseTimeLabel.setText(record.getCreateTime().format(formatter));

                originalItemsTable.setItems(FXCollections.observableArrayList(record.getItems()));

                productComboBox.setItems(FXCollections.observableArrayList(
                        record.getItems().stream().map(i -> i.getProduct().getName()).collect(Collectors.toList())
                ));

                loadReturnHistory(id);
            } else {
                showAlert("Invoice ID not found!");
                clearOrderInfo(); // clear information
            }
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid numeric ID");
        }
    }

    private void clearOrderInfo() {
        customerNameLabel.setText("-");
        customerPhoneLabel.setText("-");
        purchaseTimeLabel.setText("-");
        originalItemsTable.setItems(FXCollections.observableArrayList());

        historyReturnArea.clear();
        historyReturnArea.setPromptText("Waiting for search...");

        productComboBox.setItems(FXCollections.observableArrayList());
        productComboBox.setValue(null);
        inputReturnQty.clear();
        returnReasonField.clear();

        currentReturnList.clear();
        updateTotalRefund();
        originalRecord = null;
    }

    private void loadReturnHistory(int originalId) {
        List<CustomerRecord> history = CustomerRecordService.getInstance().getRecords().stream()
                .filter(r -> "Return".equals(r.getType()) && r.getPhone().equals("REF-" + originalId))
                .collect(Collectors.toList());

        if (history.isEmpty()) {
            historyReturnArea.setText("--- NO PREVIOUS RETURNS ---");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("===== HISTORY RETURNS FOR INVOICE #").append(originalId).append(" =====\n");
            for (CustomerRecord r : history) {
                sb.append("Date: ").append(r.getCreateTime().format(java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm"))).append("\n");
                sb.append("Items: ").append(r.getItemsSummary()).append("\n");
                sb.append("Reason: ").append(r.getCustomerName()).append("\n");
                sb.append("-------------------------------------------\n");
            }
            historyReturnArea.setText(sb.toString());
        }
    }

    // choose returned product
    @FXML
    public void addItemToCurrentReturn() {
        String prodName = productComboBox.getValue();
        String qtyText = inputReturnQty.getText();

        if (prodName == null || qtyText.isEmpty()) return;

        try {
            int inputQty = Integer.parseInt(qtyText);

            // original quantity
            OrderItem originalItem = originalRecord.getItems().stream()
                    .filter(i -> i.getProduct().getName().equals(prodName))
                    .findFirst().orElse(null);

            if (originalItem == null) return;

            // return amount
            int alreadyReturnedQty = CustomerRecordService.getInstance().getRecords().stream()
                    .filter(r -> "Return".equals(r.getType()) && r.getPhone().equals("REF-" + originalRecord.getId()))
                    .flatMap(r -> r.getItems().stream())
                    .filter(item -> item.getProduct().getName().equals(prodName))
                    .mapToInt(OrderItem::getQuantity)
                    .sum();

            // current return quantity
            int pendingReturnQty = currentReturnList.stream()
                    .filter(item -> item.getProduct().getName().equals(prodName))
                    .mapToInt(OrderItem::getQuantity)
                    .sum();

            int maxAvailableToReturn = originalItem.getQuantity() - alreadyReturnedQty - pendingReturnQty;

            if (inputQty <= 0) {
                showAlert("Quantity must be greater than 0");
                return;
            }

            if (inputQty <= maxAvailableToReturn) {
                OrderItem existing = currentReturnList.stream()
                        .filter(i -> i.getProduct().getName().equals(prodName))
                        .findFirst().orElse(null);

                if (existing != null) {
                    currentReturnList.remove(existing);
                    currentReturnList.add(new OrderItem(existing.getProduct(), existing.getQuantity() + inputQty));
                } else {
                    currentReturnList.add(new OrderItem(originalItem.getProduct(), inputQty));
                }
                updateTotalRefund();
            } else {
                showAlert("Insufficient quantity! Remaining available: " + maxAvailableToReturn);
            }
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid number");
        }
    }


    @FXML
    public void processReturn() {
        if (originalRecord == null || currentReturnList.isEmpty()) {
            showAlert("Please search for an invoice and add items to return.");
            return;
        }

        // return reason
        String reason = returnReasonField.getText().trim();
        if (reason.isEmpty()) {
            showAlert("Please enter a return reason before proceeding.");
            return;
        }

        CustomerRecord returnTicket = new CustomerRecord(
                reason,
                "REF-" + originalRecord.getId(),
                new ArrayList<>(currentReturnList),
                calculateRefundTotal(),
                "Return"
        );

        try {
            // set record to database
            CustomerRecordService.getInstance().addRecord(returnTicket);

            // add stock
            for (OrderItem item : currentReturnList) {
                Product p = ProductService.getInstance().findByName(item.getProduct().getName());
                if (p != null) {
                    p.setStock(p.getStock() + item.getQuantity());
                    ProductService.getInstance().updateProduct(p);
                }
            }

            showAlert("Return processed successfully!");
            resetReturnForm();
            // load history
            loadReturnHistory(originalRecord.getId());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error processing return: " + e.getMessage());
        }
    }

    private void resetReturnForm() {
        currentReturnList.clear();
        returnReasonField.clear();
        inputReturnQty.clear();
        productComboBox.setValue(null);
        updateTotalRefund();
    }

    private double calculateRefundTotal() {
        return currentReturnList.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    private void updateTotalRefund() {
        totalRefundLabel.setText(String.format("%.2f", calculateRefundTotal()));
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    public void goBack(ActionEvent actionEvent) {
        SceneSwitcher.switchScene("/com/example/possystem/main.fxml");
    }
}