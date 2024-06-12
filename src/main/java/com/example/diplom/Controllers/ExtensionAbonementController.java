package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.addLibraries.CreateAlert;
import com.example.diplom.addLibraries.WindowsActions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

public class ExtensionAbonementController {

    @FXML
    private TextField abonementIdField;

    @FXML
    private Spinner<Integer> minutesSpinner;

    @FXML
    private void handleSave() {
        try {
            int id = Integer.parseInt(abonementIdField.getText());
            int minutes = minutesSpinner.getValue();

            DB db = DB.getBase();
            boolean success = db.extensionAbonement(id, minutes);
            if (success) {
                CreateAlert.showAlert(Alert.AlertType.INFORMATION, "Успех", null, "Абонемент успешно продлен");
            } else {
                CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", null, "Введены неправильные данные");
            }
        } catch (NumberFormatException e) {
            CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", null, "Ошибка в поле ID абонемента");
        }
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        WindowsActions.closeWindow(actionEvent);
    }

    @FXML
    private void initialize() {
        abonementIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                abonementIdField.setText(newValue.replaceAll("[^\\d]", ""));
            }

            if (!newValue.isEmpty()) {
                try {
                    int id = Integer.parseInt(newValue);
                    DB db = DB.getBase();
                    int nominal = db.getNominalAbonement(id);
                    int balance = db.getBalanceAbonement(id);
                    int maxExtension = nominal - balance;

                    if (maxExtension > 0) {
                        minutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxExtension, 0));
                    } else {
                        minutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));
                    }
                } catch (NumberFormatException e) {
                    minutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));
                }
            } else {
                minutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));
            }
        });
    }
}
