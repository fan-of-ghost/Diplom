package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.Products.Certificate;
import com.example.diplom.addLibraries.CreateAlert;
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

    private LocalDate maxDate;

    @FXML
    void initialize() {
        dateOfReservation.setDisable(true);
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
                if (date.isBefore(today) || date.isEqual(today) || (maxDate != null && date.isAfter(maxDate))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Цвет ячейки для прошедших или текущих дат
                }
            }
        });
    }

    @FXML
    private void insertIdAndBalance() {
        // Очищаем comboboxIdCertificate
        comboboxIdCertificate.getItems().clear();

        // Получаем доступ к базе данных
        DB db = DB.getBase();

        // Получаем список доступных сертификатов из базы данных
        for (Certificate certificate : db.getActiveCertificates()) { // Изменение в методе получения сертификатов
            // Добавляем идентификатор сертификата в комбобокс
            if (certificate.getBalance() > 0) {
                comboboxIdCertificate.getItems().add(certificate.getId());
            }
        }

        // Устанавливаем слушателя для выбора сертификата
        comboboxIdCertificate.setOnAction(event -> {
            // Получаем выбранный сертификат из комбобокса
            Integer selectedCertificateId = comboboxIdCertificate.getValue();
            if (selectedCertificateId != null) {
                // Получаем баланс и дату истечения выбранного сертификата из базы данных
                int certificateBalance = db.getBalanceCertificate(selectedCertificateId);
                LocalDate expirationDate = db.getExpirationDateCertificate(selectedCertificateId);

                // Устанавливаем максимальную дату для бронирования
                maxDate = expirationDate;

                // Включаем DatePicker и обновляем DayCellFactory
                dateOfReservation.setDisable(false);
                disablePastAndCurrentDates();

                // Устанавливаем баланс сертификата как максимальное значение для спиннера
                spinnerMinutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, certificateBalance, 0));
            } else {
                CreateAlert.showAlert(Alert.AlertType.WARNING, "Выбор сертификата", "Сертификат не выбран", "Пожалуйста, выберите сертификат.");
            }
        });
    }

    @FXML
    private void reserveButton() {
        Integer selectedCertificateId = comboboxIdCertificate.getValue();
        Integer minutesToUse = spinnerMinutes.getValue();
        LocalDate reservationDate = dateOfReservation.getValue();

        if (selectedCertificateId == null) {
            CreateAlert.showAlert(Alert.AlertType.WARNING, "Ошибка бронирования", "Сертификат не выбран", "Пожалуйста, выберите сертификат для бронирования.");
            return;
        }

        if (minutesToUse == null || minutesToUse <= 0) {
            CreateAlert.showAlert(Alert.AlertType.WARNING, "Ошибка бронирования", "Некорректное количество минут", "Пожалуйста, выберите корректное количество минут.");
            return;
        }

        if (reservationDate == null) {
            CreateAlert.showAlert(Alert.AlertType.WARNING, "Ошибка бронирования", "Дата не выбрана", "Пожалуйста, выберите дату для бронирования.");
            return;
        }

        // Получаем доступ к базе данных
        DB db = DB.getBase();

        try {
            // Обновляем данные в таблице Сертификаты
            db.updateCertificate(selectedCertificateId, minutesToUse);

            // Вставляем данные в таблицу График_сертификатов
            db.addNewCertificateRace(reservationDate, minutesToUse, selectedCertificateId);

            // Показать сообщение об успешном бронировании
            CreateAlert.showAlert(Alert.AlertType.INFORMATION, "Успешное бронирование", "Бронирование прошло успешно", "Ваше бронирование на " + reservationDate + " успешно выполнено.");

            // Установить текущую дату в dateOfReservation после сохранения
            dateOfReservation.setValue(LocalDate.now());

            // Очищаем поля после успешного бронирования
            comboboxIdCertificate.setValue(null);
            spinnerMinutes.getValueFactory().setValue(0);
            dateOfReservation.setDisable(true);

        } catch (SQLException e) {
            e.printStackTrace();
            CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка базы данных", "Ошибка при выполнении запроса", "Произошла ошибка при бронировании. Попробуйте еще раз.");
        }
    }

}
