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
    private TableColumn<Abonement, Date> dateOfEndAbonement;
    @FXML
    private TableColumn<Abonement, Integer> balanceAbonement; // Добавляем колонку для баланса абонемента

    @FXML
    void initialize() {
        loadAbonements();
        setupColorForDateOfEnd();
        setupColorForBalance();
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
        loadAbonements();
    }

    public void onSaveToFileClick() {
        String downloadsPath = System.getProperty("user.home") + "/Downloads/";
        String baseFileName = "abonements";
        String fileExtension = ".csv";

        String fileName = generateUniqueFileName(downloadsPath, baseFileName, fileExtension);

        try (PrintWriter writer = new PrintWriter(new File(downloadsPath + fileName))) {
            writer.println("ID,Номинал,Дата использования,Остаток,Дата покупки,Дата истечения,Дата продления,ID Статуса,Номер клиента");

            ObservableList<Abonement> data = tableViewAbonements.getItems();

            for (Abonement abonement : data) {
                writer.println(abonement.getId() + "," +
                        abonement.getNominal() + "," +
                        abonement.getDateOfUse() + "," +
                        abonement.getBalance() + "," +
                        abonement.getDateOfBuy() + "," +
                        abonement.getDateOfEnd() + "," +
                        abonement.getDateOfRes() + "," +
                        getStatusIdByName(abonement.getStatus()) + "," +
                        abonement.getIdClient());
            }

            System.out.println("Данные успешно сохранены в файл " + fileName + " в папке \"Загрузки\"");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при сохранении данных в файл");
        }
    }

    private String generateUniqueFileName(String directory, String baseName, String extension) {
        int counter = 1;
        String fileName = baseName + extension;
        File file = new File(directory + fileName);

        while (file.exists()) {
            fileName = baseName + "_" + counter + extension;
            file = new File(directory + fileName);
            counter++;
        }

        return fileName;
    }

    private int getStatusIdByName(String statusName) {
        DB db = DB.getBase();
        return db.getStatusIdByName(statusName);
    }

    private void setupColorForDateOfEnd() {
        dateOfEndAbonement.setCellFactory(column -> {
            return new TableCell<Abonement, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle("");
                    } else {
                        setText(item.toString());
                        if (item.toLocalDate().isBefore(LocalDate.now())) {
                            setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-alignment: center;");
                        } else {
                            setStyle("-fx-alignment: center;");
                        }
                    }
                }
            };
        });
    }

    private void setupColorForBalance() {
        balanceAbonement.setCellFactory(column -> {
            return new TableCell<Abonement, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle("");
                    } else {
                        setText(item.toString());
                        if (item == 0) {
                            setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-alignment: center;");
                        } else {
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
        MenuItem extendMenuItem = new MenuItem("Продлить на 1 год");
        MenuItem sendToArchiveMenuItem = new MenuItem("Отправить в архив"); // Новый пункт меню

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

        extendMenuItem.setOnAction(event -> {
            Abonement selectedAbonement = tableViewAbonements.getSelectionModel().getSelectedItem();
            if (selectedAbonement != null) {
                try {
                    DB db = DB.getBase();
                    db.extendAbonement(selectedAbonement.getId(), 365);
                    System.out.println("Абонемент с id " + selectedAbonement.getId() + " продлен на 1 год");
                    loadAbonements();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        sendToArchiveMenuItem.setOnAction(event -> {
            Abonement selectedAbonement = tableViewAbonements.getSelectionModel().getSelectedItem();
            if (selectedAbonement != null) {
                try {
                    DB db = DB.getBase();
                    db.archiveAbonement(selectedAbonement.getId());
                    System.out.println("Абонемент с id " + selectedAbonement.getId() + " отправлен в архив");
                    loadAbonements();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        contextMenu.getItems().addAll(editMenuItem, archiveMenuItem, extendMenuItem, sendToArchiveMenuItem);

        tableViewAbonements.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                Abonement selectedAbonement = tableViewAbonements.getSelectionModel().getSelectedItem();
                if (selectedAbonement != null) {
                    try {
                        DB db = DB.getBase();
                        boolean isInactive = db.checkStateByIdAbonement(selectedAbonement.getId()).equals("disactive");
                        archiveMenuItem.setVisible(isInactive);
                        extendMenuItem.setVisible(isInactive);
                        sendToArchiveMenuItem.setVisible(selectedAbonement.getBalance() == 0);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                contextMenu.show(tableViewAbonements, event.getScreenX(), event.getScreenY());
            } else if (event.getButton() == MouseButton.PRIMARY) {
                contextMenu.hide();
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

    public void extensionAbonement() throws IOException {
        WindowsActions.openModalWindow("Продление абонемента", "extensionAbonement.fxml");
    }

    public void loadCertificates(ActionEvent actionEvent) throws IOException {
        WindowsActions.changeWindow(actionEvent,"Сертификаты (гл. администратор)","mainAdminCertificates.fxml");
    }

    public void toLogOut(ActionEvent actionEvent) throws IOException {
        WindowsActions.changeWindow(actionEvent,"Авторизация", "login.fxml");
    }
}
