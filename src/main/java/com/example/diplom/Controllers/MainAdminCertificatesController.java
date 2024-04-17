package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.Products.Abonement;
import com.example.diplom.Products.Certificate;
import com.example.diplom.addLibraries.WindowsActions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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


}
