package com.example.possystem.controller;

import com.example.possystem.model.CustomerRecord;
import com.example.possystem.model.CustomerRecordManager;
import com.example.possystem.util.SceneSwitcher;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;

public class CustomerListController {

    @FXML private TableView<CustomerRecord> customerTable;
    @FXML private TableColumn<CustomerRecord, String> nameCol;
    @FXML private TableColumn<CustomerRecord, String> phoneCol;
    @FXML private TableColumn<CustomerRecord, String> itemsCol;
    @FXML private TableColumn<CustomerRecord, Double> totalCol;
    @FXML private TableColumn<CustomerRecord, String> typeCol;
    @FXML private TableColumn<CustomerRecord, String> timeCol;

    @FXML
    public void initialize() {
        ObservableList<CustomerRecord> records = CustomerRecordManager.getRecords();
        customerTable.setItems(records);

        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomerName()));
        phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        itemsCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getItemsSummary()));
        totalCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalAmount()).asObject());
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        timeCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));
    }

    public void goBack() {
        SceneSwitcher.switchScene("/com/example/possystem/main.fxml");
    }
}