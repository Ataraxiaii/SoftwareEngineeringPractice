package com.example.possystem.controller;

import com.example.possystem.model.Product;
import com.example.possystem.service.ProductService;
import com.example.possystem.util.SceneSwitcher;
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

    private ProductService productService = ProductService.getInstance();
    private ObservableList<Product> allProducts;

    @FXML
    public void initialize() {
        tableView.setItems(productService.getProducts()); // load from database
        tableView.setItems(allProducts);

        imageCol.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    @FXML
    public void addProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/possystem/product_add.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Add Product");
            stage.showAndWait();

            ProductAddController controller = loader.getController();
            Product product = controller.getResult();
            if (product != null) {
                productService.addProduct(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void editProduct() {
        Product selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/possystem/product_add.fxml")
            );
            Parent root = loader.load();

            ProductAddController controller = loader.getController();
            controller.setProduct(selected);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modify Product");
            stage.showAndWait();

            Product newProduct = controller.getResult();
            if (newProduct != null) {
                productService.updateProduct(selected, newProduct); // update database
                tableView.refresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteProduct() {
        Product selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            productService.removeProduct(selected); // delete database
        }
    }

    @FXML
    public void goBack() {
        SceneSwitcher.switchScene("/com/example/possystem/main.fxml");
        System.out.println("Go back to main interface");
    }

    @FXML
    public void search() {
        String keyword = searchField.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            tableView.setItems(allProducts);
            return;
        }

        ObservableList<Product> filtered = allProducts.filtered(p ->
                p.getName().toLowerCase().contains(keyword)
        );
        tableView.setItems(filtered);
    }
}

