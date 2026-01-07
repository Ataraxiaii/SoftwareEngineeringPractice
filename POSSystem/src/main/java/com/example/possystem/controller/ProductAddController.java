package com.example.possystem.controller;

import com.example.possystem.model.Product;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ProductAddController {

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private ComboBox<String> statusBox;
    @FXML private ImageView imageView;

    private String imagePath;
    private Product result;

    public Product getResult() {
        return result;
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
        result = new Product(
                nameField.getText(),
                Double.parseDouble(priceField.getText()),
                Integer.parseInt(stockField.getText()),
                statusBox.getValue(),
                imagePath
        );
        ((Stage) nameField.getScene().getWindow()).close();
    }
}
