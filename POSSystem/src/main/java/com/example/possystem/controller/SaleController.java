package com.example.possystem.controller;

import com.example.possystem.model.Order;
import com.example.possystem.model.OrderItem;
import com.example.possystem.model.Product;
import com.example.possystem.service.ProductService;
import com.example.possystem.util.SceneSwitcher;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SaleController {

    @FXML private ListView<Product> productListView;
    @FXML private TextField quantityField;

    @FXML private TableView<OrderItem> cartTable;
    @FXML private TableColumn<OrderItem, String> cartNameCol;
    @FXML private TableColumn<OrderItem, Double> cartPriceCol;
    @FXML private TableColumn<OrderItem, Integer> cartQtyCol;
    @FXML private TableColumn<OrderItem, Double> cartSubCol;

    @FXML private Label totalLabel;

    private ObservableList<OrderItem> cartList = FXCollections.observableArrayList();
    private ProductService productService = ProductService.getInstance();

    @FXML
    public void initialize() {
        productListView.setItems(productService.getProducts()); // load from database

        cartNameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProduct().getName()));
        cartPriceCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getProduct().getPrice()).asObject());
        cartQtyCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        cartSubCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());

        cartTable.setItems(cartList);
    }

    // add to Cashier
    public void addToCart() {
        Product product = productListView.getSelectionModel().getSelectedItem();
        if (product == null) return;

        int qty;
        try {
            qty = Integer.parseInt(quantityField.getText());
        } catch (Exception e) {
            alert("Invalid number format");
            return;
        }

        // check out stock
        if (qty > product.getStock()) {
            alert("Insufficient stock, current inventory:" + product.getStock());
            return;
        }

        // add to Cashier and reduce the stock
        cartList.add(new OrderItem(product, qty));
        product.setStock(product.getStock() - qty);

        productListView.refresh();

        updateTotal();
        quantityField.clear();
    }

    // delete from Cashier
    public void removeItem() {
        OrderItem item = cartTable.getSelectionModel().getSelectedItem();
        if (item == null) return;

        // Deleting will roll back
        Product p = item.getProduct();
        p.setStock(p.getStock() + item.getQuantity());

        cartList.remove(item);
        productListView.refresh();
        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (OrderItem item : cartList) {
            total += item.getSubtotal();
        }
        totalLabel.setText(String.valueOf(total));
    }

    public void submit() {
        if (cartList.isEmpty()) {
            alert("Cashier is empty");
            return;
        }

        double total = 0;
        for (OrderItem item : cartList) {
            total += item.getSubtotal();
        }

        Order order = new Order(cartList, total);

        SceneSwitcher.switchScene(
                "/com/example/possystem/payment.fxml",
                controller -> ((PaymentController) controller).setOrder(order)
        );
        System.out.println("Go to the payment screen (Next)");
    }

    public void goBack() {
        rollbackInventory(); //  restore stock when leaving
        SceneSwitcher.switchScene("/com/example/possystem/main.fxml");
    }

    public void rollbackInventory() {
        for (OrderItem item : cartList) {
            Product p = item.getProduct();
            // restore cart quantity to memory object
            p.setStock(p.getStock() + item.getQuantity());
        }
        cartList.clear();
        productListView.refresh();
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.showAndWait();
    }
}
