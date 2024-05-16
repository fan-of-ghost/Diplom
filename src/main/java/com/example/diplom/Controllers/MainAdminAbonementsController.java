package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.Products.Abonement;
import com.example.diplom.addLibraries.WindowsActions;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import com.example.diplom.addLibraries.DataExchanger;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MainAdminAbonementsController {
    @FXML
    private TableView<Abonement> tableViewAbonements;
    @FXML
    private TableColumn <Date, Date> dateOfEndAbonement;

    @FXML
    void initialize() {
        loadAbonements();

        setupColorForDateOfEnd();

        setupContextMenu();
    }

    @FXML
    private void loadAbonements() {
        DB db = DB.getBase();
        try {
            List<Abonement> abonements = db.getAbonements();
            tableViewAbonements.getItems().clear();
            tableViewAbonements.getItems().addAll(abonements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onAddClick() throws IOException {
        WindowsActions.openModalWindow("Новый абонемент", "abonementForm.fxml");
        // Загружаем абонементы для обновления
        loadAbonements();
    }

    public void onSaveToFileClick() {
        // Получаем путь к папке "Загрузки" для текущего пользователя
        String downloadsPath = System.getProperty("user.home") + "/Downloads/";

        try (PrintWriter writer = new PrintWriter(new File(downloadsPath + "abonements.csv"))) {
            // Записываем заголовки столбцов
            writer.println("ID,Номинал,Дата использования,Остаток,Дата покупки,Дата истечения,Дата продления,Статус,Номер клиента");

            // Получаем данные из TableView
            ObservableList<Abonement> data = tableViewAbonements.getItems();

            // Проходим по каждому абонементу и записываем его данные в CSV
            for (Abonement abonement : data) {
                writer.println(abonement.getId() + "," +
                        abonement.getNominal() + "," +
                        abonement.getDateOfUse() + "," +
                        abonement.getBalance() + "," +
                        abonement.getDateOfBuy() + "," +
                        abonement.getDateOfEnd() + "," +
                        abonement.getDateOfRes() + "," +
                        abonement.getStatus() + "," +
                        abonement.getIdClient());
            }

            System.out.println("Данные успешно сохранены в файл abonements.csv в папке \"Загрузки\"");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при сохранении данных в файл");
        }
    }

    private void setupColorForDateOfEnd() {
        dateOfEndAbonement.setCellFactory(column -> {
            return new TableCell<Date, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);

                    // Проверяем, пуста ли ячейка или равна ли ей null
                    if (empty || item == null) {
                        setText(null); // Очищаем текст ячейки
                        setGraphic(null); // Очищаем графическое содержимое ячейки
                        setStyle(""); // Сбрасываем стиль
                    } else {
                        // Устанавливаем текст ячейки
                        setText(item.toString());

                        // Проверяем, что дата продления меньше текущей даты
                        if (item.toLocalDate().isBefore(LocalDate.now())) {
                            // Если условие выполнено, устанавливаем красный цвет фона ячейки, белый цвет текста и выравнивание текста по центру
                            setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-alignment: center;");
                        } else {
                            // Иначе используем стандартный стиль
                            setStyle("-fx-alignment: center;");
                        }
                    }
                }
            };
        });
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editMenuItem = new MenuItem("Посмотреть клиента");
        MenuItem archiveMenuItem = new MenuItem("Архивировать абонемент");

        // Добавляем обработчики действий для каждого пункта меню

        editMenuItem.setOnAction(event -> {
            Abonement selectedAbonement = tableViewAbonements.getSelectionModel().getSelectedItem();
            if (selectedAbonement != null) {
                try {
                    DataExchanger dataExchanger = DataExchanger.getInstance();
                    dataExchanger.setId(selectedAbonement.getId());
                    WindowsActions.openWindow("Информация о клиенте", "clientInfoAbonements.fxml");
                    System.out.println(selectedAbonement.getId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        archiveMenuItem.setOnAction(event -> {
            Abonement selectedAbonement = tableViewAbonements.getSelectionModel().getSelectedItem();
            if (selectedAbonement != null) {
                try {
                    DB db = DB.getBase();
                    db.archiveAbonement(selectedAbonement.getId());
                    System.out.println("В архив отправился абонемент с id " + selectedAbonement.getId());
                    tableViewAbonements.getItems().remove(selectedAbonement);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Добавляем пункты меню
        contextMenu.getItems().addAll(editMenuItem, archiveMenuItem);

        // Устанавливаем обработчик для открытия контекстного меню
        tableViewAbonements.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                // Проверяем, удовлетворяет ли выбранный абонемент условию для отображения пункта "Архивировать абонемент"
                Abonement selectedAbonement = tableViewAbonements.getSelectionModel().getSelectedItem();
                if (selectedAbonement != null) {
                    try {
                        DB db = DB.getBase();
                        boolean isInactive = db.checkStateByIdAbonement(selectedAbonement.getId()).equals("disactive");
                        archiveMenuItem.setVisible(isInactive); // Устанавливаем видимость пункта меню в зависимости от результата проверки
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                contextMenu.show(tableViewAbonements, event.getScreenX(), event.getScreenY());
            }
        });
    }

    public void onLoadToFileClick() throws IOException {
        WindowsActions.openModalWindow("Импорт таблиц из csv", "importTables.fxml");
    }

    public void openModalWindowArchiveToAbonements() throws IOException {
        WindowsActions.openModalWindow("Архив просроченных абонементов", "archiveToAbonements.fxml");
    }

    public void clientForPeriod() throws IOException {
        WindowsActions.openModalWindow("Сотрудники за период", "exportClientsForPeriodAbonement.fxml");
    }

    public void openReservation() throws IOException {
        WindowsActions.openModalWindow("Бронирование заезда по абонементу", "reservationForAbonement.fxml");
    }

    public void openSchedule() throws IOException {
        WindowsActions.openModalWindow("Расписание абонементов", "scheduleAbonements.fxml");
    }
}