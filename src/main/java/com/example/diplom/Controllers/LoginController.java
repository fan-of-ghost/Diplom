package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.addLibraries.CreateAlert;
import com.example.diplom.addLibraries.WindowsActions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML
    public Label viewPass;
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    public TextField passwordText;

    @FXML
    void view_pass_click() {
        //Проверка: видимо поле с паролем или нет
        //Если видимо
        if (password.isVisible()) {
            passwordText.setVisible(true);
            passwordText.setText(password.getText());
            password.setVisible(false);
        }
        //Если не видимо
        else {
            password.setVisible(true);
            password.setText(passwordText.getText());
            passwordText.setVisible(false);
        }
    }

    @FXML
    void enter_button_click(ActionEvent event) throws IOException, ClassNotFoundException {
        //trim() - убирает пробелы до и после текста
        String log = login.getText().trim();
        String pass;

        if (password.isVisible()) {
            pass = password.getText().trim();
        } else {
            pass = passwordText.getText().trim();
        }

        if (!log.isEmpty()&&!pass.isEmpty()) {
            DB db = DB.getBase();

            if (db.checkRole(log,pass) == 1) {
                WindowsActions.changeWindow(event,"Список заказов гл админа","mainAdminMenu.fxml");
            }
            else if (db.checkRole(log,pass) == 2) {
                WindowsActions.changeWindow(event,"Список заказов админа","nonMainAdminMenu.fxml");
            }
            else {
                CreateAlert.showAlert(Alert.AlertType.ERROR, "Авторизация","Ошибка","Неверный логин или пароль");
            }
        }
        else {
            CreateAlert.showAlert(Alert.AlertType.WARNING, "Предупреждение","Введены не все данные","Проверьте заполненность всех полей");
        }
    }
}