package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.Products.Abonement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReservationForAbonement {
    @FXML
    private Spinner<Integer> spinnerMinutes;
    @FXML
    private ComboBox<Integer> comboboxIdAbonement;
    @FXML
    private DatePicker dateOfReservation;

    @FXML
    void initialize() {

        disablePastAndCurrentDates();

        insertIdAndBalance();

    }
    @FXML
    private void disablePastAndCurrentDates() {
        dateOfReservation.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                if (date.isBefore(today) || date.isEqual(today)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Цвет ячейки для прошедших или текущих дат
                }
            }
        });
    }

    @FXML
    private void insertIdAndBalance() {
        // Очищаем comboboxIdAbonement
        comboboxIdAbonement.getItems().clear();

        // Получаем доступ к базе данных
        DB db = DB.getBase();

        // Получаем список доступных абонементов из базы данных
        for (Abonement abonement : db.getAbonements()) {
            // Добавляем идентификатор абонемента в комбобокс
            comboboxIdAbonement.getItems().add(abonement.getId());
        }

        // Устанавливаем слушателя для выбора абонемента
        comboboxIdAbonement.setOnAction(event -> {
            // Получаем выбранный абонемент из комбобокса
            int selectedAbonementId = comboboxIdAbonement.getValue();

            // Получаем баланс выбранного абонемента из базы данных
            int abonementBalance = db.getBalanceAbonement(selectedAbonementId);

            // Устанавливаем баланс абонемента как максимальное значение для спиннера
            spinnerMinutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, abonementBalance, 0));
        });
    }

    @FXML
    private void reserveButton() {
        int selectedAbonementId = comboboxIdAbonement.getValue();
        int minutesToUse = spinnerMinutes.getValue();
        LocalDate reservationDate = dateOfReservation.getValue();

        // Получаем доступ к базе данных
        DB db = DB.getBase();

        try {
            // Вставляем данные в таблицу График_абонементов
            db.insertReservation(selectedAbonementId, reservationDate);

            // Обновляем данные в таблице Абонементы
            db.updateAbonement(selectedAbonementId, minutesToUse);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
