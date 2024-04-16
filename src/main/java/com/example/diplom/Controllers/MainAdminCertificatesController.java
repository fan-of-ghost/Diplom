package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.Products.Abonement;
import com.example.diplom.Products.Certificate;
import com.example.diplom.addLibraries.WindowsActions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainAdminCertificatesController {
    @FXML
    private TableView<Certificate> tableViewCertificates;

    @FXML
    void initialize() {
        loadCertificates();
    }

    private void loadCertificates() {
        DB db = DB.getBase();
        try {
            List<Certificate> certificate = db.getCertificates();
            tableViewCertificates.getItems().clear();
            tableViewCertificates.getItems().addAll(certificate);
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

        // Создаем объект ObjectMapper для преобразования данных в JSON
        ObjectMapper objectMapper = new ObjectMapper();
        // Устанавливаем настройку для отступов (indent) для лучшей читаемости
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            // Преобразуем данные в JSON и записываем их в файл в папке "Загрузки"
            objectMapper.writeValue(new File(downloadsPath + "certificates.json"), tableViewCertificates.getItems());
            System.out.println("Данные успешно сохранены в файл certificates.json в папке \"Загрузки\"");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при сохранении данных в файл");
        }
    }

}
