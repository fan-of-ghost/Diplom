package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.Products.Abonement;
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

public class ArchiveToAbonementsController {
    @FXML
    private TableView<Abonement> tableViewAbonements;
    @FXML
    void initialize() {
        loadAbonements();

        setupContextMenu();
    }
    private void loadAbonements() {
        DB db = DB.getBase();
        try {
            List<Abonement> abonements = db.getInactiveAbonements();
            tableViewAbonements.getItems().clear();
            tableViewAbonements.getItems().addAll(abonements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editMenuItem = new MenuItem("Посмотреть клиента");
        editMenuItem.setOnAction(event -> {
            Abonement selectedAbonement = tableViewAbonements.getSelectionModel().getSelectedItem();
            if (selectedAbonement != null) {
                // Действия при выборе пункта "Редактировать"
                try {
                    DataExchanger dataExchanger = DataExchanger.getInstance();
                    dataExchanger.setId(selectedAbonement.getId());
                    WindowsActions.openWindow("Информация о клиенте", "clientInfo.fxml");
                    System.out.println(selectedAbonement.getId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        MenuItem archiveMenuItem = new MenuItem("Разархивировать абонемент");
        archiveMenuItem.setOnAction(event -> {
            Abonement selectedAbonement = tableViewAbonements.getSelectionModel().getSelectedItem();
            if (selectedAbonement != null) {
                try {
                    DB db = DB.getBase();
                    db.unArchiveAbonement(selectedAbonement.getId());
                    System.out.println("В архив отправился абонемент с id " + selectedAbonement.getId());
                    tableViewAbonements.getItems().remove(selectedAbonement);
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        contextMenu.getItems().addAll(editMenuItem, archiveMenuItem);

        // Установка обработчика для открытия контекстного меню при щелчке правой кнопкой мыши
        tableViewAbonements.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(tableViewAbonements, event.getScreenX(), event.getScreenY());
            }
        });
    }
}
