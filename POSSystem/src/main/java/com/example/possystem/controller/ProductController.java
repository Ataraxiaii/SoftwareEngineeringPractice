package com.example.possystem.controller;

import com.example.possystem.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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
    @FXML
    private TableColumn<Product, ImageView> imageCol;
    @FXML
    private TextField searchField;


    private ObservableList<Product> allProducts = FXCollections.observableArrayList();

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        imageCol.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        productList.add(new Product("薯片", 10.0, 100, "在售", null));
        productList.add(new Product("可乐", 8.0, 50, "在售", null));

        tableView.setItems(productList);
        allProducts.addAll(productList);
        tableView.setItems(productList);
    }

    public void addProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/possystem/product_add.fxml")
            );
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Product");
            stage.showAndWait();

            ProductAddController controller = loader.getController();
            Product product = controller.getResult();
            if (product != null) {
                productList.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void search() {
        String keyword = searchField.getText().toLowerCase();
        productList.clear();

        for (Product p : allProducts) {
            if (p.getName().toLowerCase().contains(keyword)) {
                productList.add(p);
            }
        }
    }

}

