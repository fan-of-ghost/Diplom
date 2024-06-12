package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.addLibraries.CreateAlert;
import com.example.diplom.addLibraries.WindowsActions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ClientFormController {
    @FXML
    private TextField txtSurname;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPatronymic;
    @FXML
    private TextField txtPhoneNumber;
    @FXML
    private TextField txtEmail;

    @FXML
    void initialize() {
        setupTextFields();  // Добавляем настройку полей ввода
    }

    private void setupTextFields() {
        setupTextField(txtSurname);
        setupTextField(txtName);
        setupTextField(txtPatronymic);
        setupPhoneNumberField(txtPhoneNumber);
    }

    private void setupTextField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Zа-яА-ЯёЁ]*") || newValue.length() > 50) {
                textField.setText(oldValue);
            }
        });
    }

    private void setupPhoneNumberField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*") || newValue.length() > 11) {
                textField.setText(oldValue);
            }
        });
    }

    public void closeForm(ActionEvent actionEvent) {
        WindowsActions.closeWindow(actionEvent);
    }

    public void saveClient(ActionEvent actionEvent) {
        String surname = txtSurname.getText();
        String name = txtName.getText();
        String patronymic = txtPatronymic.getText();
        String phoneNumber = txtPhoneNumber.getText();
        String email = txtEmail.getText();

        // Проверяем, что все поля заполнены
        if (surname.isEmpty() || name.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
            CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Пустые поля", "Пожалуйста, заполните все поля");
            return;
        }

        // Добавляем нового клиента в базу данных
        DB.getBase().addNewClient(surname, name, patronymic, phoneNumber, email);

        // Закрываем окно формы
        WindowsActions.closeWindow(actionEvent);

        // Отображаем сообщение об успешном добавлении нового клиента
        CreateAlert.showAlert(Alert.AlertType.INFORMATION, "Создание клиента", "Успешно", "Новый клиент добавлен");
    }
}
