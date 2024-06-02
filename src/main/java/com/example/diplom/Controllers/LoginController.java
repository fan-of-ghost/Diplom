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
        // Проверка: видимо поле с паролем или нет
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
        // trim() - убирает пробелы до и после текста
        String log = login.getText().trim();
        String pass;

        if (password.isVisible()) {
            pass = password.getText().trim();
        } else {
            pass = passwordText.getText().trim();
        }

        if (!log.isEmpty() && !pass.isEmpty()) {
            try {
                DB db = DB.getBase();

                int role = db.checkRole(log, pass);
                if (role == 1) {
                    WindowsActions.changeWindow(event, "Меню главного админа", "mainAdminMenu.fxml");
                } else if (role == 2) {
                    WindowsActions.changeWindow(event, "Меню админа", "nonMainAdminMenu.fxml");
                } else {
                    CreateAlert.showAlert(Alert.AlertType.ERROR, "Авторизация", "Ошибка", "Неверный логин или пароль");
                }
            } catch (IOException e) {
                e.printStackTrace();
                CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка ввода-вывода", "Ошибка при переключении окон", "Произошла ошибка при переключении окон. Попробуйте позже.");
            } catch (Exception e) {
                e.printStackTrace();
                CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка базы данных", "Ошибка при подключении к базе данных", "Не удалось подключиться к базе данных. Попробуйте позже.");
            }
        } else {
            CreateAlert.showAlert(Alert.AlertType.WARNING, "Предупреждение", "Введены не все данные", "Проверьте заполненность всех полей.");
        }
    }
}
