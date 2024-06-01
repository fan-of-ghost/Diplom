package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.addLibraries.CreateAlert;
import com.example.diplom.addLibraries.WindowsActions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ExtensionAbonementController {

    @FXML
    private TextField abonementIdField;

    @FXML
    private TextField minutesField;

    @FXML
    private void handleSave() {
        try {
            int id = Integer.parseInt(abonementIdField.getText());
            int minutes = Integer.parseInt(minutesField.getText());

            DB db = DB.getBase();
            boolean success = db.extensionAbonement(id, minutes);
            if (success) {
                CreateAlert.showAlert(Alert.AlertType.INFORMATION, "Успех", null, "Абонемент успешно продлен");
            } else {
                CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", null, "Введены неправильные данные");
            }
        } catch (NumberFormatException e) {
            CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", null, "Ошибка в поле(-ях) ID абонемента и количество");
        }
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        WindowsActions.closeWindow(actionEvent);
    }
}
