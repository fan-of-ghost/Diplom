package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.Products.Abonement;
import com.example.diplom.addLibraries.CreateAlert;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;

public class ReservationForAbonementController {
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
            if (abonement.getBalance() > 0) {
                comboboxIdAbonement.getItems().add(abonement.getId());
            }
        }

        // Устанавливаем слушателя для выбора абонемента
        comboboxIdAbonement.setOnAction(event -> {
            // Получаем выбранный абонемент из комбобокса
            Integer selectedAbonementId = comboboxIdAbonement.getValue();
            if (selectedAbonementId != null) {
                // Получаем баланс выбранного абонемента из базы данных
                int abonementBalance = db.getBalanceAbonement(selectedAbonementId);

                // Устанавливаем баланс абонемента как максимальное значение для спиннера
                spinnerMinutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, abonementBalance, 0));
            } else {
                CreateAlert.showAlert(Alert.AlertType.WARNING, "Выбор абонемента", "Абонемент не выбран", "Пожалуйста, выберите абонемент.");
            }
        });
    }

    @FXML
    private void reserveButton() {
        Integer selectedAbonementId = comboboxIdAbonement.getValue();
        Integer minutesToUse = spinnerMinutes.getValue();
        LocalDate reservationDate = dateOfReservation.getValue();

        if (selectedAbonementId == null) {
            CreateAlert.showAlert(Alert.AlertType.WARNING, "Ошибка бронирования", "Абонемент не выбран", "Пожалуйста, выберите абонемент для бронирования.");
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
            // Вставляем данные в таблицу График_абонементов
            db.insertReservationAbonement(selectedAbonementId, reservationDate);

            // Обновляем данные в таблице Абонементы
            db.updateAbonement(selectedAbonementId, minutesToUse);

            // Показать сообщение об успешном бронировании
            CreateAlert.showAlert(Alert.AlertType.INFORMATION, "Успешное бронирование", "Бронирование прошло успешно", "Ваше бронирование на " + reservationDate + " успешно выполнено.");

        } catch (SQLException e) {
            e.printStackTrace();
            CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка базы данных", "Ошибка при выполнении запроса", "Произошла ошибка при бронировании. Попробуйте еще раз.");
        }
    }
}
