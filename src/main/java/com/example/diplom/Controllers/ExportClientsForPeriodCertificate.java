package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.Products.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Set;

public class ExportClientsForPeriodCertificate {
    @FXML
    private Button exportToCSVButton;
    @FXML
    private DatePicker firstDate;
    @FXML
    private DatePicker secondDate;

    @FXML
    void exportToCSV() {
        LocalDate startDate = firstDate.getValue();
        LocalDate endDate = secondDate.getValue();

        // Получаем путь к папке "Загрузки" для текущего пользователя
        String downloadsPath = System.getProperty("user.home") + "/Downloads/";

        // Определяем базовое имя файла и начальное значение счетчика инкремента
        String baseFileName = "clientsForPeriodCertificate";
        int incrementCounter = 1;

        // Генерируем уникальное имя файла
        String fileName = baseFileName + ".csv";
        File outputFile = new File(downloadsPath + fileName);
        while (outputFile.exists()) {
            fileName = baseFileName + "_" + incrementCounter + ".csv";
            outputFile = new File(downloadsPath + fileName);
            incrementCounter++;
        }

        try (PrintWriter writer = new PrintWriter(outputFile)) {
            // Записываем заголовки столбцов
            writer.println("ID,Фамилия,Имя,Отчество,Контактный телефон,Адрес электронной почты");

            // Получаем клиентов за выбранный период
            Set<Client> clients = DB.getBase().getClientsForPeriodCertificate(startDate, endDate);

            // Проходим по каждому клиенту и записываем его данные в CSV
            for (Client client : clients) {
                writer.println(client.getId() + "," +
                        client.getSurname() + "," +
                        client.getName() + "," +
                        client.getPatronymic() + "," +
                        client.getPhoneNumber() + "," +
                        client.getEmail());
            }

            System.out.println("Данные успешно сохранены в файл " + fileName + " в папке \"Загрузки\"");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при сохранении данных в файл");
        }
    }
}
