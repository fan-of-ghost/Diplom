package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.Products.Abonement;
import com.example.diplom.Products.Certificate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;

public class ReservationForCertificateController {
    @FXML
    private Spinner<Integer> spinnerMinutes;
    @FXML
    private ComboBox<Integer> comboboxIdCertificate;
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
        comboboxIdCertificate.getItems().clear();

        // Получаем доступ к базе данных
        DB db = DB.getBase();

        // Получаем список доступных абонементов из базы данных
        for (Certificate certificate : db.getCertificates()) {
            // Добавляем идентификатор абонемента в комбобокс
            comboboxIdCertificate.getItems().add(certificate.getId());
        }

        // Устанавливаем слушателя для выбора абонемента
        comboboxIdCertificate.setOnAction(event -> {
            // Получаем выбранный абонемент из комбобокса
            int selectedCertificateId = comboboxIdCertificate.getValue();

            // Получаем баланс выбранного абонемента из базы данных
            int abonementBalance = db.getBalanceCertificate(selectedCertificateId);

            // Устанавливаем баланс абонемента как максимальное значение для спиннера
            spinnerMinutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, abonementBalance, 0));
        });
    }

    @FXML
    private void reserveButton() {
        int selectedCertificateId = comboboxIdCertificate.getValue();
        int minutesToUse = spinnerMinutes.getValue();
        LocalDate reservationDate = dateOfReservation.getValue();

        // Получаем доступ к базе данных
        DB db = DB.getBase();

        try {
            // Вставляем данные в таблицу График_абонементов
            db.insertReservationCertificate(selectedCertificateId, reservationDate);

            // Обновляем данные в таблице Абонементы
            db.updateCertificate(selectedCertificateId, minutesToUse);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
