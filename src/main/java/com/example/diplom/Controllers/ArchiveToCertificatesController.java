package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.Products.Abonement;
import com.example.diplom.Products.Certificate;
import com.example.diplom.addLibraries.DataExchanger;
import com.example.diplom.addLibraries.WindowsActions;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ArchiveToCertificatesController {
    @FXML
    private TableView<Certificate> tableViewCertificates;
    @FXML
    void initialize() {
        loadAbonements();

        setupContextMenu();
    }
    private void loadAbonements() {
        DB db = DB.getBase();
        try {
            List<Certificate> certificates = db.getInactiveCertificates();
            tableViewCertificates.getItems().clear();
            tableViewCertificates.getItems().addAll(certificates);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editMenuItem = new MenuItem("Посмотреть клиента");
        editMenuItem.setOnAction(event -> {
            Certificate selectedCertificate = tableViewCertificates.getSelectionModel().getSelectedItem();
            if (selectedCertificate != null) {
                // Действия при выборе пункта "Редактировать"
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

        MenuItem archiveMenuItem = new MenuItem("Разархивировать сертификат");
        archiveMenuItem.setOnAction(event -> {
            Certificate selectedCertificate = tableViewCertificates.getSelectionModel().getSelectedItem();
            if (selectedCertificate != null) {
                try {
                    DB db = DB.getBase();
                    db.unArchiveCertificate(selectedCertificate.getId());
                    System.out.println("В архив отправился сертификат с id " + selectedCertificate.getId());
                    tableViewCertificates.getItems().remove(selectedCertificate);
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        contextMenu.getItems().addAll(editMenuItem, archiveMenuItem);

        // Установка обработчика для открытия контекстного меню при щелчке правой кнопкой мыши
        tableViewCertificates.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(tableViewCertificates, event.getScreenX(), event.getScreenY());
            }
        });
    }
}
