package com.example.possystem.controller;

import com.example.possystem.model.CustomerRecord;
import com.example.possystem.model.CustomerRecordManager;
import com.example.possystem.service.CustomerRecordService;
import com.example.possystem.util.SceneSwitcher;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
        ObservableList<CustomerRecord> records = CustomerRecordService.getInstance().getRecords();
        customerTable.setItems(records);

        // show customer name for return record
        nameCol.setCellValueFactory(data -> {
            CustomerRecord r = data.getValue();
            if ("Return".equals(r.getType())) {
                // "REF-27"  get number 27
                int originalId = Integer.parseInt(r.getPhone().replace("REF-", ""));
                // search record according id
                CustomerRecord original = records.stream()
                        .filter(rec -> rec.getId() == originalId && "Shopping".equals(rec.getType()))
                        .findFirst().orElse(null);

                if (original != null) {
                    return new SimpleStringProperty(original.getCustomerName() + " (Return)");
                }
            }
            return new SimpleStringProperty(r.getCustomerName());
        });

        //nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomerName()));
        itemsCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getItemsSummary()));
        totalCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalAmount()).asObject());
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        timeCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));

        phoneCol.setCellValueFactory(data -> {
            CustomerRecord r = data.getValue();
            if ("Return".equals(r.getType())) {
                int originalId = Integer.parseInt(r.getPhone().replace("REF-", ""));
                CustomerRecord original = records.stream()
                        .filter(rec -> rec.getId() == originalId && "Shopping".equals(rec.getType()))
                        .findFirst().orElse(null);

                // calculate the amount of returning
                long count = records.stream()
                        .filter(rec -> "Return".equals(rec.getType()) && r.getPhone().equals(rec.getPhone()))
                        .filter(rec -> !rec.getCreateTime().isAfter(r.getCreateTime()))
                        .count();

                String phoneStr = (original != null) ? original.getPhone() : "Unknown";
                return new SimpleStringProperty(phoneStr + " [Attempt: " + count + "]");
            }
            return new SimpleStringProperty(r.getPhone());
        });
    }

    @FXML
    public void deleteRecord() {
        CustomerRecord selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this customer record?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            // delete database
            CustomerRecordService.getInstance().removeRecord(selected);
            customerTable.getItems().remove(selected);
        }
    }

    public void goBack() {
        SceneSwitcher.switchScene("/com/example/possystem/main.fxml");
    }
}