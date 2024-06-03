package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.addLibraries.DataExchanger;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RacesCertificatesOnDayController {

    @FXML
    private ListView<String> racesListView;

    @FXML
    public void initialize() {
        LocalDate selectedDate = DataExchanger.getInstance().getDate();
        loadRacesForDate(selectedDate);
        addContextMenu();
    }

    private void loadRacesForDate(LocalDate date) {
        try {
            DB db = DB.getBase();
            Connection conn = db.getDbConnection();
            Statement stmt = conn.createStatement();
            String query = "SELECT ga.id_графика, ga.дата_использования, ga.затраченное_время_в_минутах, " +
                    "cl.фамилия, cl.имя, cl.отчество, ab.id_сертификата " +
                    "FROM График_сертификатов ga " +
                    "JOIN Сертификаты ab ON ga.id_сертификата = ab.id_сертификата " +
                    "JOIN Клиенты cl ON ab.id_клиента = cl.id_клиента " +
                    "WHERE ga.дата_использования = '" + date + "'";

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String raceInfo = String.format("ID: %d | Дата: %s | Длительность: %d мин. | Клиент: %s %s %s | ID Сертификата: %d",
                        rs.getInt("id_графика"),
                        rs.getDate("дата_использования").toString(),
                        rs.getInt("затраченное_время_в_минутах"),
                        rs.getString("фамилия"),
                        rs.getString("имя"),
                        rs.getString("отчество"),
                        rs.getInt("id_сертификата"));
                racesListView.getItems().add(raceInfo);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem cancelRaceItem = new MenuItem("Отменить заезд");

        cancelRaceItem.setOnAction(event -> {
            String selectedItem = racesListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                try {
                    // Используем регулярное выражение для извлечения данных
                    Pattern pattern = Pattern.compile("ID: (\\d+) \\| Дата: ([^\\|]+) \\| Длительность: (\\d+) мин\\. \\| Клиент: [^\\|]+ \\| ID Сертификата: (\\d+)");
                    Matcher matcher = pattern.matcher(selectedItem);

                    if (!matcher.find()) {
                        throw new IllegalArgumentException("Неверный формат строки: " + selectedItem);
                    }

                    int raceId = Integer.parseInt(matcher.group(1));
                    LocalDate raceDate = LocalDate.parse(matcher.group(2));
                    int spentTime = Integer.parseInt(matcher.group(3));
                    int certificateId = Integer.parseInt(matcher.group(4));

                    // Проверка, является ли дата прошедшей
                    if (raceDate.isBefore(LocalDate.now())) {
                        Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                        warningAlert.setTitle("Отмена невозможна");
                        warningAlert.setHeaderText("Невозможно отменить заезд");
                        warningAlert.setContentText("Вы не можете отменить заезд на прошедшую дату.");
                        warningAlert.showAndWait();
                        return;
                    }

                    // Подтверждение отмены
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Подтверждение отмены");
                    confirmationAlert.setHeaderText("Вы уверены, что хотите отменить этот заезд?");
                    confirmationAlert.setContentText(selectedItem);

                    Optional<ButtonType> result = confirmationAlert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        DB db = DB.getBase();
                        Connection conn = db.getDbConnection();

                        // Обновляем остаток времени в абонементе
                        String updateCertificateQuery = "UPDATE Сертификаты SET остаток_в_минутах = остаток_в_минутах + ? WHERE id_сертификата = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateCertificateQuery)) {
                            updateStmt.setInt(1, spentTime);
                            updateStmt.setInt(2, certificateId);
                            updateStmt.executeUpdate();
                        }

                        // Удаляем запись о заезде
                        String deleteRaceQuery = "DELETE FROM График_сертификатов WHERE id_графика = ?";
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteRaceQuery)) {
                            deleteStmt.setInt(1, raceId);
                            deleteStmt.executeUpdate();
                        }

                        // Удаляем элемент из ListView
                        racesListView.getItems().remove(selectedItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        contextMenu.getItems().add(cancelRaceItem);

        racesListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(racesListView, event.getScreenX(), event.getScreenY());
            }
        });
    }


}
