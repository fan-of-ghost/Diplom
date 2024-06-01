package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.addLibraries.DataExchanger;
import com.example.diplom.addLibraries.WindowsActions;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class ScheduleAbonementsController {
    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private ComboBox<String> monthComboBox;
    @FXML
    private GridPane calendarGrid;

    private List<LocalDate> abonementDates;
    private LocalDate selectedDate;
    private ContextMenu contextMenu;
    private Label previouslySelectedLabel;

    @FXML
    public void initialize() {
        populateYearComboBox();
        populateMonthComboBox();
        loadAbonementDatesFromDatabase();

        yearComboBox.setOnAction(event -> updateCalendar());
        monthComboBox.setOnAction(event -> updateCalendar());

        updateCalendar();
    }

    private void populateYearComboBox() {
        for (int year = 2020; year <= LocalDate.now().getYear() + 1; year++) {
            yearComboBox.getItems().add(year);
        }
        yearComboBox.setValue(LocalDate.now().getYear());
    }

    private void populateMonthComboBox() {
        String[] months = {
                "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
        };
        monthComboBox.getItems().addAll(months);
        monthComboBox.setValue(months[LocalDate.now().getMonthValue() - 1]);
    }

    private void loadAbonementDatesFromDatabase() {
        abonementDates = new ArrayList<>();
        try {
            DB db = DB.getBase();
            Connection conn = db.getDbConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT дата_использования FROM График_абонементов");

            while (rs.next()) {
                abonementDates.add(LocalDate.parse(rs.getString("дата_использования")));
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createContextMenu(boolean isHighlighted) {
        contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Посмотреть заезды");
        MenuItem item2 = new MenuItem("Option 2");

        if (!isHighlighted) {
            item1.setVisible(false);
        }

        item1.setOnAction(event -> {
            System.out.println("Option 1 selected for date: " + selectedDate);
            DataExchanger.getInstance().setDate(selectedDate);
            try {
                WindowsActions.openWindow("Заезды на " + selectedDate, "racesOnDay.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        item2.setOnAction(event -> {
            System.out.println("Option 2 selected for date: " + selectedDate);
            // Add your action here
        });

        contextMenu.getItems().addAll(item1, item2);
    }



    private void updateCalendar() {
        calendarGrid.getChildren().clear();

        int year = yearComboBox.getValue();
        int month = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        int dayOfWeekValue = firstDayOfMonth.getDayOfWeek().getValue();

        int dayCounter = 1;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane cell = new StackPane();
                cell.setStyle("-fx-border-color: lightgrey; -fx-border-width: 1;");
                cell.setPrefSize(40, 40);

                if (i == 0 && j < dayOfWeekValue - 1) {
                    calendarGrid.add(cell, j, i);
                } else if (dayCounter <= yearMonth.lengthOfMonth()) {
                    LocalDate date = LocalDate.of(year, month, dayCounter);
                    boolean isHighlighted = abonementDates.contains(date);
                    Label dayLabel = createDayLabel(year, month, dayCounter, isHighlighted);

                    if (isHighlighted) {
                        dayLabel.setStyle("-fx-background-color: yellow; -fx-font-size: 16px; -fx-padding: 10px;");
                    } else {
                        dayLabel.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
                    }

                    cell.getChildren().add(dayLabel);
                    calendarGrid.add(cell, j, i);
                    dayCounter++;
                } else {
                    calendarGrid.add(cell, j, i);
                }
            }
        }
    }

    private Label createDayLabel(int year, int month, int day, boolean isHighlighted) {
        Label dayLabel = new Label(String.valueOf(day));
        dayLabel.setOnMouseClicked(event -> {
            selectedDate = LocalDate.of(year, month, day);
            if (event.getButton().name().equals("SECONDARY")) {
                if (previouslySelectedLabel != null) {
                    // Reset the style of the previously selected label
                    if (abonementDates.contains(LocalDate.of(year, month, Integer.parseInt(previouslySelectedLabel.getText())))) {
                        previouslySelectedLabel.setStyle("-fx-background-color: yellow; -fx-font-size: 16px; -fx-padding: 10px;");
                    } else {
                        previouslySelectedLabel.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
                    }
                }
                // Highlight the newly selected date
                dayLabel.setStyle("-fx-background-color: lightgrey; -fx-font-size: 16px; -fx-padding: 10px;");
                previouslySelectedLabel = dayLabel;
                createContextMenu(isHighlighted);  // Create the context menu based on whether the date is highlighted
                contextMenu.show(dayLabel, javafx.geometry.Side.RIGHT, 0, 0);
            }
        });
        return dayLabel;
    }
}
