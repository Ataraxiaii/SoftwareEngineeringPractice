package com.example.possystem;

import com.example.possystem.util.DBUtil;
import com.example.possystem.util.SceneSwitcher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        DBUtil.initDB();
        SceneSwitcher.setStage(stage);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/possystem/main.fxml")
        );
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("POS System");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
