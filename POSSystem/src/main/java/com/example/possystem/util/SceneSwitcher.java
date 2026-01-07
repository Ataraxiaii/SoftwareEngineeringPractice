package com.example.possystem.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class SceneSwitcher {

    private static Stage stage;

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void switchScene(String fxmlPath) {
        switchScene(fxmlPath, null);
    }

    public static void switchScene(String fxmlPath, Consumer<Object> init) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneSwitcher.class.getResource(fxmlPath)
            );
            Scene scene = new Scene(loader.load());

            if (init != null) {
                init.accept(loader.getController());
            }

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}