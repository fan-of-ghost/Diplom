package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.Products.Certificate;
import com.example.diplom.addLibraries.DataExchanger;
import com.example.diplom.addLibraries.WindowsActions;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class MainAdminCertificatesController {
    @FXML
    private TableColumn<Date, Date> dateOfEndCertificate;
    @FXML
    private TableView<Certificate> tableViewCertificates;

    @FXML
    void initialize() {
        loadCertificates();
        setupColorForDateOfEnd();
        setupContextMenu();
    }

    @FXML
    private void loadCertificates() {
        DB db = DB.getBase();
        try {
            List<Certificate> certificates = db.getCertificates();
            tableViewCertificates.getItems().clear();
            tableViewCertificates.getItems().addAll(certificates);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onAddClick() throws IOException {
        WindowsActions.openModalWindow("Новый сертификат", "certificateForm.fxml");
        // Загружаем абонементы для обновления
        loadCertificates();
    }

    public void onSaveToFileClick() {
        // Получаем путь к папке "Загрузки" для текущего пользователя
        String downloadsPath = System.getProperty("user.home") + "/Downloads/";

        try (PrintWriter writer = new PrintWriter(new File(downloadsPath + "certificates.csv"))) {
            // Записываем заголовки столбцов
            writer.println("ID,Номинал,Дата использования,Остаток,Дата покупки,Дата истечения,Статус,Номер клиента");

            // Получаем данные из TableView
            ObservableList<Certificate> data = tableViewCertificates.getItems();

            // Проходим по каждому сертификату и записываем его данные в CSV
            for (Certificate certificate : data) {
                writer.println(certificate.getId() + "," +
                        certificate.getNominal() + "," +
                        certificate.getDateOfUse() + "," +
                        certificate.getBalance() + "," +
                        certificate.getDateOfBuy() + "," +
                        certificate.getDateOfEnd() + "," +
                        certificate.getStatus() + "," +
                        certificate.getIdClient());
            }

            System.out.println("Данные успешно сохранены в файл certificates.csv в папке \"Загрузки\"");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при сохранении данных в файл");
        }
    }

    private void setupColorForDateOfEnd() {
        dateOfEndCertificate.setCellFactory(column -> {
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
        MenuItem archiveMenuItem = new MenuItem("Архивировать сертификат");

        // Добавляем обработчики действий для каждого пункта меню

        editMenuItem.setOnAction(event -> {
            Certificate selectedCertificate = tableViewCertificates.getSelectionModel().getSelectedItem();
            if (selectedCertificate != null) {
                try {
                    DataExchanger dataExchanger = DataExchanger.getInstance();
                    dataExchanger.setId(selectedCertificate.getId());
                    WindowsActions.openWindow("Информация о клиенте", "clientInfoCertificates.fxml");
                    System.out.println(selectedCertificate.getId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        archiveMenuItem.setOnAction(event -> {
            Certificate selectedCertificate = tableViewCertificates.getSelectionModel().getSelectedItem();
            if (selectedCertificate != null) {
                try {
                    DB db = DB.getBase();
                    db.archiveCertificate(selectedCertificate.getId());
                    System.out.println("В архив отправился сертификат с id " + selectedCertificate.getId());
                    tableViewCertificates.getItems().remove(selectedCertificate);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Добавляем пункты меню
        contextMenu.getItems().addAll(editMenuItem, archiveMenuItem);

        // Устанавливаем обработчик для открытия контекстного меню
        tableViewCertificates.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                // Проверяем, удовлетворяет ли выбранный сертификат условию для отображения пункта "Архивировать сертификат"
                Certificate selectedCertificate = tableViewCertificates.getSelectionModel().getSelectedItem();
                if (selectedCertificate != null) {
                    try {
                        DB db = DB.getBase();
                        boolean isInactive = db.checkStateByIdCertificate(selectedCertificate.getId()).equals("disactive");
                        archiveMenuItem.setVisible(isInactive); // Устанавливаем видимость пункта меню в зависимости от результата проверки
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                contextMenu.show(tableViewCertificates, event.getScreenX(), event.getScreenY());
            } else if (event.getButton() == MouseButton.PRIMARY) {
                contextMenu.hide(); // Скрываем контекстное меню при клике левой кнопкой мыши
            }
        });
    }

    public void onLoadToFileClick() throws IOException {
        WindowsActions.openModalWindow("Импорт таблиц из csv", "importTables.fxml");
    }

    public void openModalWindowArchiveToCertificates() throws IOException {
        WindowsActions.openModalWindow("Архив просроченных сертификатов", "archiveToCertificates.fxml");
    }

    public void clientForPeriod() throws IOException {
        WindowsActions.openModalWindow("Сотрудники за период", "exportClientsForPeriodCertificate.fxml");
    }

    public void openReservation() throws IOException {
        WindowsActions.openModalWindow("Бронирование заезда по абонементу", "reservationForCertificate.fxml");
    }
}
