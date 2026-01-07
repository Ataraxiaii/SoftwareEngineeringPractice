module com.example.possystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.example.possystem to javafx.fxml;
    opens com.example.possystem.controller to javafx.fxml;
    opens com.example.possystem.model to javafx.base;

    exports com.example.possystem;
}