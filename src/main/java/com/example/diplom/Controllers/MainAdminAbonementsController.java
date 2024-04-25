package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.Products.Abonement;
import com.example.diplom.addLibraries.WindowsActions;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
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
                            setStyle("");
                        }
                    }
                }
            };
        });
    }

}