package com.example.possystem;

import com.example.possystem.util.SceneSwitcher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/possystem/main.fxml")
        );
        Scene scene = new Scene(loader.load(), 400, 400);
        stage.setTitle("POS System");
        stage.setScene(scene);
        stage.show();
        SceneSwitcher.setStage(stage);
    }
}
