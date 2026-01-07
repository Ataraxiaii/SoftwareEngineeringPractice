package com.example.possystem.controller;

import com.example.possystem.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductController {

    @FXML
    private TableView<Product> tableView;

    @FXML
    private TableColumn<Product, String> nameCol;
    @FXML
    private TableColumn<Product, Double> priceCol;
    @FXML
    private TableColumn<Product, Integer> stockCol;
    @FXML
    private TableColumn<Product, String> statusCol;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        productList.add(new Product("薯片", 10.0, 100, "在售", null));
        productList.add(new Product("可乐", 8.0, 50, "在售", null));

        tableView.setItems(productList);
    }

    public void addProduct() {
        productList.add(new Product("New Product", 0, 0, "Available", null));
    }

    public void editProduct() {
        Product selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setPrice(selected.getPrice() + 1);
            tableView.refresh();
        }
    }

    public void deleteProduct() {
        Product selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            productList.remove(selected);
        }
    }

    public void goBack() {
        System.out.println("Go back to main interface");
    }
}
