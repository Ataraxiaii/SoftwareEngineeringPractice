package com.example.possystem.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CustomerRecordManager {

    private static final ObservableList<CustomerRecord> records = FXCollections.observableArrayList();

    public static ObservableList<CustomerRecord> getRecords() {
        return records;
    }

    public static void addRecord(CustomerRecord record) {
        records.add(record);
    }
}
