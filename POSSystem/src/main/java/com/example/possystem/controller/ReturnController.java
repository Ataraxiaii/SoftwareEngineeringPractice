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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ReturnController {

    @FXML
    private TableView<CustomerRecord> recordTable;
    @FXML
    private TableColumn<CustomerRecord, String> recordNameCol;
    @FXML
    private TableColumn<CustomerRecord, String> recordPhoneCol;
    @FXML
    private TableColumn<CustomerRecord, String> recordItemsCol;
    @FXML
    private TableColumn<CustomerRecord, Double> recordTotalCol;
    @FXML
    private TableColumn<CustomerRecord, String> recordTimeCol;

    @FXML
    private TableView<OrderItem> originalItemsTable;
    @FXML
    private TableColumn<OrderItem, String> originalNameCol;
    @FXML
    private TableColumn<OrderItem, Double> originalPriceCol;
    @FXML
    private TableColumn<OrderItem, Integer> originalQtyCol;

    @FXML
    private TableView<OrderItem> returnItemsTable;
    @FXML
    private TableColumn<OrderItem, String> returnNameCol;
    @FXML
    private TableColumn<OrderItem, Double> returnPriceCol;
    @FXML
    private TableColumn<OrderItem, Integer> returnQtyCol;
    @FXML private TableColumn<OrderItem, Double> returnSubtotalCol;

    @FXML private TextField searchField;
    @FXML private Label totalRefundLabel;
    @FXML private TextField returnReasonField;

    private CustomerRecord selectedRecord;
    private final ObservableList<OrderItem> returnItems = FXCollections.observableArrayList();
    private ProductService productService = ProductService.getInstance();

    @FXML
    public void initialize() {
        // 初始化客户记录表格
        recordNameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCustomerName()));
        recordPhoneCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPhone()));
        recordItemsCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getItemsSummary()));
        recordTotalCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getTotalAmount()).asObject());
        recordTimeCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

        recordTable.setItems(CustomerRecordService.getInstance().getRecords());

        // 初始化原始商品表格
        originalNameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProduct().getName()));
        originalPriceCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getProduct().getPrice()).asObject());
        originalQtyCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());

        // 初始化退货商品表格
        returnNameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProduct().getName()));
        returnPriceCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getProduct().getPrice()).asObject());
        returnQtyCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        returnSubtotalCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());

        returnItemsTable.setItems(returnItems);

        // 监听记录选择
        recordTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        loadRecordDetails(newSelection);
                    }
                }
        );
    }

    private void loadRecordDetails(CustomerRecord record) {
        selectedRecord = record;
        originalItemsTable.setItems(FXCollections.observableArrayList(record.getItems()));
        returnItems.clear();
        updateTotalRefund();
    }

    public void searchRecords(ActionEvent actionEvent) {
        String keyword = searchField.getText().toLowerCase().trim();
        ObservableList<CustomerRecord> allRecords = CustomerRecordService.getInstance().getRecords();

        if (keyword.isEmpty()) {
            recordTable.setItems(allRecords);
            return;
        }

        ObservableList<CustomerRecord> filtered = allRecords.filtered(record ->
                record.getCustomerName().toLowerCase().contains(keyword) ||
                        record.getPhone().contains(keyword) ||
                        record.getItemsSummary().toLowerCase().contains(keyword)
        );
        recordTable.setItems(filtered);
    }

    public void goBack(ActionEvent actionEvent) {
        System.out.println("Go to the main screen");
        SceneSwitcher.switchScene("/com/example/possystem/main.fxml");
    }

    @FXML
    public void addToReturn(ActionEvent actionEvent) {
        OrderItem selected = originalItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an item from the original purchase");
            return;
        }

        // 检查是否已经在退货列表中
        for (OrderItem item : returnItems) {
            if (item.getProduct().getName().equals(selected.getProduct().getName())) {
                showAlert("This item is already in the return list");
                return;
            }
        }

        // 创建退货项（数量默认为1，可以修改）
        OrderItem returnItem = new OrderItem(selected.getProduct(), 1);
        returnItems.add(returnItem);
        updateTotalRefund();
    }

    private void updateTotalRefund() {
        double total = 0;
        for (OrderItem item : returnItems) {
            total += item.getSubtotal();
        }
        totalRefundLabel.setText(String.format("Total Refund: $%.2f", total));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.showAndWait();
    }

    @FXML
    public void removeFromReturn(ActionEvent actionEvent) {
        OrderItem selected = returnItemsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            returnItems.remove(selected);
            updateTotalRefund();
        }
    }

    @FXML
    public void updateReturnQuantity(ActionEvent actionEvent) {
        OrderItem selected = returnItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getQuantity()));
        dialog.setTitle("Update Quantity");
        dialog.setHeaderText("Update return quantity for " + selected.getProduct().getName());
        dialog.setContentText("Enter new quantity:");

        dialog.showAndWait().ifPresent(quantityStr -> {
            try {
                int newQty = Integer.parseInt(quantityStr);
                if (newQty < 1) {
                    showAlert("Quantity must be at least 1");
                    return;
                }

                // 检查不超过原始购买数量
                int originalQty = getOriginalQuantity(selected.getProduct());
                if (newQty > originalQty) {
                    showAlert("Cannot return more than originally purchased (" + originalQty + ")");
                    return;
                }

                // 更新数量（需要创建新对象，因为OrderItem是不可变的）
                returnItems.remove(selected);
                returnItems.add(new OrderItem(selected.getProduct(), newQty));
                updateTotalRefund();
            } catch (NumberFormatException e) {
                showAlert("Invalid quantity");
            }
        });
    }

    private int getOriginalQuantity(Product product) {
        for (OrderItem item : selectedRecord.getItems()) {
            if (item.getProduct().getName().equals(product.getName())) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    @FXML
    public void processReturn(ActionEvent actionEvent) {
        if (selectedRecord == null) {
            showAlert("Please select a purchase record first");
            return;
        }

        if (returnItems.isEmpty()) {
            showAlert("Please add items to return");
            return;
        }

        if (returnReasonField.getText().trim().isEmpty()) {
            showAlert("Please enter a return reason");
            return;
        }

        // 确认对话框
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Return");
        confirm.setHeaderText("Process Return");
        confirm.setContentText("Are you sure you want to process this return?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // 1. 更新库存（增加库存）
            for (OrderItem item : returnItems) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productService.updateProduct(product);
            }

            // 2. 创建退货记录
            double refundTotal = calculateRefundTotal();
            CustomerRecord returnRecord = new CustomerRecord(
                    selectedRecord.getCustomerName(),
                    selectedRecord.getPhone(),
                    new ArrayList<>(returnItems),
                    refundTotal,
                    "Returning"
            );

            // 3. 保存退货记录
            CustomerRecordService.getInstance().addRecord(returnRecord);

            // 4. 显示成功消息
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Return Processed");
            success.setHeaderText("Return completed successfully");
            success.setContentText(String.format("Refund amount: $%.2f\nReason: %s",
                    refundTotal, returnReasonField.getText()));
            success.showAndWait();

            // 5. 重置界面
            resetForm();
        }
    }

    private void resetForm() {
        returnItems.clear();
        originalItemsTable.setItems(FXCollections.observableArrayList());
        returnReasonField.clear();
        totalRefundLabel.setText("Total Refund: $0.00");
        selectedRecord = null;
    }

    private double calculateRefundTotal() {
        double total = 0;
        for (OrderItem item : returnItems) {
            total += item.getSubtotal();
        }
        return total;
    }
}
