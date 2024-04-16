package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.addLibraries.WindowsActions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class AbonementFormController {
    @FXML
    private TextField txtNominal;
    @FXML
    private Button saveFormButton;
    @FXML
    private DatePicker datePickerOfBay;
    @FXML
    private ComboBox<String> comboBoxIdStatus;
    @FXML
    private ComboBox<String> comboBoxIdClient;

    @FXML
    void initialize() {
        insertStatus();
        insertClient();
    }

    public void closeForm(ActionEvent actionEvent) {
        WindowsActions.closeWindow(actionEvent);
    }

    private void insertStatus() {
        try {
            Connection connection = DB.getBase().getDbConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT `название` FROM `Статусы`");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String data = resultSet.getString("название");
                comboBoxIdStatus.getItems().add(data);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertClient() {
        // Очищаем comboBoxIdClient
        comboBoxIdClient.getItems().clear();

        try {
            Connection connection = DB.getBase().getDbConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT `адрес_электронной_почты` FROM `Клиенты`");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String data = resultSet.getString("адрес_электронной_почты");
                comboBoxIdClient.getItems().add(data);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void addNewClient() throws IOException {
        WindowsActions.openModalWindow("Новый клиент", "clientForm.fxml");
        insertClient();
    }

    @FXML
    void saveAbonement(ActionEvent event) {
        // Получаем данные из формы
        int nominal = Integer.parseInt(txtNominal.getText());
        LocalDate dateOfBuy = datePickerOfBay.getValue();

        // Получаем id статуса и клиента
        int statusId = DB.getBase().getStatusIdByName(comboBoxIdStatus.getValue());
        int clientId = DB.getBase().getClientIdByEmail(comboBoxIdClient.getValue());

        // Вставляем новый абонемент в базу данных
        DB.getBase().addAbonement(nominal, statusId, clientId, Date.valueOf(dateOfBuy));

        // Отображаем сообщение об успешном сохранении
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успешно");
        alert.setHeaderText(null);
        alert.setContentText("Абонемент успешно сохранен");
        alert.showAndWait();

        // Закрываем окно формы
        WindowsActions.closeWindow(event);
    }
}