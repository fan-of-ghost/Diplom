package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.Products.Abonement;
import com.example.diplom.addLibraries.WindowsActions;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainAdminAbonementsController {
    @FXML
    private TableView<Abonement> tableViewAbonements;
    @FXML
    private TableColumn idAbonement;
    @FXML
    private TableColumn nominalAbonement;
    @FXML
    private TableColumn dateOfUseAbonement;
    @FXML
    private TableColumn balanceAbonement;
    @FXML
    private TableColumn dateOfBuyAbonement;
    @FXML
    private TableColumn dateOfEndAbonement;
    @FXML
    private TableColumn dateOfResAbonement;
    @FXML
    private TableColumn statusAbonement;
    @FXML
    private TableColumn idClientAbonement;

    @FXML
    void initialize() {
        loadAbonements();
    }

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

        // Создаем объект ObjectMapper для преобразования данных в JSON
        ObjectMapper objectMapper = new ObjectMapper();
        // Устанавливаем настройку для отступов (indent) для лучшей читаемости
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            // Преобразуем данные в JSON и записываем их в файл в папке "Загрузки"
            objectMapper.writeValue(new File(downloadsPath + "abonements.json"), tableViewAbonements.getItems());
            System.out.println("Данные успешно сохранены в файл abonements.json в папке \"Загрузки\"");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при сохранении данных в файл");
        }
    }

}