package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.addLibraries.CreateAlert;
import com.example.diplom.addLibraries.WindowsActions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class LoginController {
    @FXML
    public Label viewPass;
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    @FXML
    private TextField passwordText;

    @FXML
    void view_pass_click() {
        if (password.isVisible()) {
            passwordText.setVisible(true);
            passwordText.setText(password.getText());
            password.setVisible(false);
        } else {
            password.setVisible(true);
            password.setText(passwordText.getText());
            passwordText.setVisible(false);
        }
    }

    @FXML
    void enter_button_click(ActionEvent event) throws IOException, ClassNotFoundException {
        String log = login.getText().trim();
        String pass;

        if (password.isVisible()) {
            pass = password.getText().trim();
        } else {
            pass = passwordText.getText().trim();
        }

        if (validateCredentials(log, pass)) {
            int role = checkUserRole(log, pass);
            if (role == 1) {
                WindowsActions.changeWindow(event, "Меню главного админа", "mainAdminMenu.fxml");
            } else if (role == 2) {
                WindowsActions.changeWindow(event, "Меню админа", "nonMainAdminMenu.fxml");
            } else {
                CreateAlert.showAlert(Alert.AlertType.ERROR, "Авторизация", "Ошибка", "Неверный логин или пароль");
            }
        } else {
            CreateAlert.showAlert(Alert.AlertType.WARNING, "Предупреждение", "Введены не все данные", "Проверьте заполненность всех полей.");
        }
    }

    boolean validateCredentials(String log, String pass) {
        return !log.isEmpty() && !pass.isEmpty();
    }

    int checkUserRole(String log, String pass) throws ClassNotFoundException {
        DB db = DB.getBase();
        return db.checkRole(log, pass);
    }
}
