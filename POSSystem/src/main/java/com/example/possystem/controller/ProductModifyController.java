package com.example.possystem.controller;

import com.example.possystem.model.Product;
import com.example.possystem.service.ProductService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ProductModifyController {

    @FXML
    private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private ComboBox<String> statusBox;
    @FXML private ImageView imageView;

    private Product product;
    private String imagePath;
    private boolean updated = false;

    public void setProduct(Product product) {
        this.product = product;

        nameField.setText(product.getName());
        priceField.setText(String.valueOf(product.getPrice()));
        stockField.setText(String.valueOf(product.getStock()));
        statusBox.setValue(product.getStatus());

        imagePath = product.getImagePath();
        if (imagePath != null) {
            imageView.setImage(new Image(imagePath));
        }
    }

    public boolean isUpdated() {
        return updated;
    }

    public void chooseImage() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg")
        );
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            imagePath = file.toURI().toString();
            imageView.setImage(new Image(imagePath));
        }
    }

    public void confirm() {
        product.setName(nameField.getText());
        product.setPrice(Double.parseDouble(priceField.getText()));
        product.setStock(Integer.parseInt(stockField.getText()));
        product.setImagePath(imagePath);

        ProductService.getInstance().updateProduct(product);
        updated = true;

        close();
    }

    private void close() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
}

