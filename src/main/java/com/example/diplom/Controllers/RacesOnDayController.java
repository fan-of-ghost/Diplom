package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.addLibraries.DataExchanger;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class RacesOnDayController {

    @FXML
    private ListView<String> racesListView;

    @FXML
    public void initialize() {
        LocalDate selectedDate = DataExchanger.getInstance().getDate();
        loadRacesForDate(selectedDate);
    }

    private void loadRacesForDate(LocalDate date) {
        try {
            DB db = DB.getBase();
            Connection conn = db.getDbConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM График_абонементов WHERE дата_использования = '" + date + "'");

            while (rs.next()) {
                String raceInfo = "Race ID: " + rs.getInt("id_графика") + ", Time: " + rs.getDate("дата_использования");
                racesListView.getItems().add(raceInfo);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
