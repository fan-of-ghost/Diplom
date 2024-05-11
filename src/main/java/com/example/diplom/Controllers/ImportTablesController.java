package com.example.diplom.Controllers;
import com.example.diplom.DB;
import com.example.diplom.addLibraries.CreateAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class ImportTablesController {
    @FXML
    private Label markRace;
    @FXML
    private Button importButtonRace;
    @FXML
    private Button importButtonAbonement;
    @FXML
    private Label markAbonement;
    @FXML
    private Label markCertificate;
    @FXML
    private Button importButtonCertificate;
    @FXML
    private Label markClient;
    @FXML
    private Button importButtonClient;
    @FXML
    private Button selectButtonRace;
    @FXML
    private Label fileNameLabelRace;
    @FXML
    private Button selectButtonAbonement;
    @FXML
    private Label fileNameLabelAbonement;
    @FXML
    private Button selectButtonCertificate;
    @FXML
    private Label fileNameLabelCertificate;
    @FXML
    private Label fileNameLabelClient;
    @FXML
    private Button selectButtonClient;
    private File selectedFileClient;
    private File selectedFileCertificate;
    private File selectedFileAbonement;
    private File selectedFileRace;

    @FXML
    private void selectFileClient() {
        // Создание объекта FileChooser
        FileChooser fileChooser = new FileChooser();

        // Настройка заголовка диалогового окна
        fileChooser.setTitle("Выберите файл");

        // Установка фильтра расширений файлов
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        // Получение основной сцены из кнопки
        Stage stage = (Stage) selectButtonClient.getScene().getWindow();

        // Открытие диалогового окна и получение выбранного файла
        selectedFileClient = fileChooser.showOpenDialog(stage);

        // Обработка выбранного файла
        if (selectedFileClient != null) {
            fileNameLabelClient.setText(selectedFileClient.getName());
            fileNameLabelClient.setVisible(true);
        } else {
            fileNameLabelClient.setText("");
            fileNameLabelClient.setVisible(false);
        }
    }

    @FXML
    private void selectFileCertificate() {
        // Создание объекта FileChooser
        FileChooser fileChooser = new FileChooser();

        // Настройка заголовка диалогового окна
        fileChooser.setTitle("Выберите файл");

        // Установка фильтра расширений файлов
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        // Получение основной сцены из кнопки
        Stage stage = (Stage) selectButtonCertificate.getScene().getWindow();

        // Открытие диалогового окна и получение выбранного файла
        selectedFileCertificate = fileChooser.showOpenDialog(stage);

        // Обработка выбранного файла
        if (selectedFileCertificate != null) {
            fileNameLabelCertificate.setText(selectedFileCertificate.getName());
            fileNameLabelCertificate.setVisible(true);
        } else {
            fileNameLabelCertificate.setText("");
            fileNameLabelCertificate.setVisible(false);
        }
    }

    @FXML
    private void selectFileAbonement() {
        // Создание объекта FileChooser
        FileChooser fileChooser = new FileChooser();

        // Настройка заголовка диалогового окна
        fileChooser.setTitle("Выберите файл");

        // Установка фильтра расширений файлов
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        // Получение основной сцены из кнопки
        Stage stage = (Stage) selectButtonAbonement.getScene().getWindow();

        // Открытие диалогового окна и получение выбранного файла
        selectedFileAbonement = fileChooser.showOpenDialog(stage);

        // Обработка выбранного файла
        if (selectedFileAbonement != null) {
            fileNameLabelAbonement.setText(selectedFileAbonement.getName());
            fileNameLabelAbonement.setVisible(true);
        } else {
            fileNameLabelAbonement.setText("");
            fileNameLabelAbonement.setVisible(false);
        }
    }

    @FXML
    private void selectFileRace() {
        // Создание объекта FileChooser
        FileChooser fileChooser = new FileChooser();

        // Настройка заголовка диалогового окна
        fileChooser.setTitle("Выберите файл");

        // Установка фильтра расширений файлов
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        // Получение основной сцены из кнопки
        Stage stage = (Stage) selectButtonRace.getScene().getWindow();

        // Открытие диалогового окна и получение выбранного файла
        selectedFileRace = fileChooser.showOpenDialog(stage);

        // Обработка выбранного файла
        if (selectedFileRace != null) {
            fileNameLabelRace.setText(selectedFileRace.getName());
            fileNameLabelRace.setVisible(true);
        } else {
            fileNameLabelRace.setText("");
            fileNameLabelRace.setVisible(false);
        }
    }

    @FXML
    private void importClient() {
        // Получаем выбранный файл
        File selectedFile = selectedFileClient;

        // Проверяем, что файл выбран
        if (selectedFile != null) {
            // Показываем кнопку "Импорт", так как файл выбран
            importButtonClient.setVisible(true);

            try (Scanner scanner = new Scanner(selectedFile)) {
                // Пропускаем первую строку, если это заголовок
                if (scanner.hasNextLine()) {
                    scanner.nextLine(); // пропускаем заголовок
                }

                // Создаем объект базы данных
                DB db = DB.getBase();

                // Создаем строку для хранения совпадающих идентификаторов
                StringBuilder duplicateIds = new StringBuilder();

                // Открываем соединение с базой данных
                try (Connection connection = db.getDbConnection()) {
                    // Читаем данные из файла и вставляем их в базу данных
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine().trim(); // удаляем пробелы в начале и в конце строки
                        if (line.isEmpty()) {
                            continue; // пропускаем пустые строки
                        }
                        // Удаляем кавычки из строки
                        line = line.replaceAll("\"", "");
                        String[] data = line.split(","); // предполагается, что данные разделены запятой

                        // Предполагается, что данные в файле имеют следующий порядок: фамилия, имя, отчество, телефон, email
                        if (data.length == 6) {
                            int id = Integer.parseInt(data[0]);
                            String surname = data[1];
                            String name = data[2];
                            String patronymic = data[3];
                            String phoneNumber = data[4];
                            String email = data[5];

                            // Проверяем, существует ли запись с таким же id
                            if (!db.clientExists(id)) {
                                // Если записи с таким id не существует, добавляем нового клиента в базу данных
                                db.addNewClient(id, surname, name, patronymic, phoneNumber, email);
                                markClient.setVisible(true);
                            } else {
                                // Если запись с таким id уже существует, добавляем id в строку с совпадающими идентификаторами
                                duplicateIds.append(id).append(", ");
                            }
                        } else {
                            // Если данные в файле неправильные, выводим сообщение об ошибке
                            System.err.println("Неправильный формат данных в файле: " + line);
                            CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Неправильный формат", "Неправильный формат данных в файле: \n" + line);
                        }
                    }

                    // После завершения цикла проверяем, были ли найдены дубликаты
                    if (duplicateIds.length() > 0) {
                        // Удаляем последнюю запятую и пробел
                        duplicateIds.delete(duplicateIds.length() - 2, duplicateIds.length());
                        // Выводим сообщение об ошибке только если найдены дубликаты
                        System.err.println("Записи с идентификаторами " + duplicateIds + " уже существуют в базе данных.");
                        CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Дублирование записи", "Записи с идентификаторами " + duplicateIds + " уже существуют в базе данных.");
                    }

                    // Все данные успешно импортированы
                    System.out.println("Данные успешно импортированы в базу данных.");
                } catch (ClassNotFoundException e) {
                    // Обработка ошибок соединения с базой данных
                    System.err.println("Ошибка при соединении с базой данных: " + e.getMessage());
                    CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Ошибка при соединении с базой данных", "");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (FileNotFoundException e) {
                // Обработка ошибок открытия файла
                System.err.println("Файл не найден: " + e.getMessage());
                CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Файл не найден", "");
            }
        } else {
            // Если файл не выбран, выводим сообщение об ошибке
            System.err.println("Файл не выбран.");
            CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Файл не выбран", "");
        }
    }

    @FXML
    private void importCertificate() {
        // Получаем выбранный файл
        File selectedFile = selectedFileCertificate;

        // Проверяем, что файл выбран
        if (selectedFile != null) {
            // Показываем кнопку "Импорт", так как файл выбран
            importButtonCertificate.setVisible(true);

            try (Scanner scanner = new Scanner(selectedFile)) {
                // Пропускаем первую строку, если это заголовок
                if (scanner.hasNextLine()) {
                    scanner.nextLine(); // пропускаем заголовок
                }

                // Создаем объект базы данных
                DB db = DB.getBase();

                // Создаем строку для хранения совпадающих идентификаторов
                StringBuilder duplicateIds = new StringBuilder();

                // Открываем соединение с базой данных
                try (Connection connection = db.getDbConnection()) {
                    // Читаем данные из файла и вставляем их в базу данных
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine().trim(); // удаляем пробелы в начале и в конце строки
                        if (line.isEmpty()) {
                            continue; // пропускаем пустые строки
                        }
                        // Удаляем кавычки из строки
                        line = line.replaceAll("\"", "");
                        String[] data = line.split(","); // предполагается, что данные разделены запятой

                        // Предполагается, что данные в файле имеют следующий порядок: фамилия, имя, отчество, телефон, email
                        if (data.length == 8) {
                            int id = Integer.parseInt(data[0]);
                            int nominal = Integer.parseInt(data[1]);
                            LocalDate dateOfUse = LocalDate.parse(data[2]);
                            int balance = Integer.parseInt(data[3]);
                            LocalDate dateOfEnd = LocalDate.parse(data[4]);
                            LocalDate dateOfBuy = LocalDate.parse(data[5]);
                            int idStatus = Integer.parseInt(data[6]);
                            int idClient = Integer.parseInt(data[7]);

                            // Проверяем, существует ли запись с таким же id
                            if (!db.certificateExists(id)) {
                                // Если записи с таким id не существует, добавляем нового клиента в базу данных
                                db.addNewCertificate(id, nominal, dateOfUse, balance, dateOfEnd, dateOfBuy, idStatus, idClient);
                                markCertificate.setVisible(true);
                            } else {
                                // Если запись с таким id уже существует, добавляем id в строку с совпадающими идентификаторами
                                duplicateIds.append(id).append(", ");
                            }
                        } else {
                            // Если данные в файле неправильные, выводим сообщение об ошибке
                            System.err.println("Неправильный формат данных в файле: " + line);
                            CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Неправильный формат", "Неправильный формат данных в файле: \n" + line);
                        }
                    }

                    // После завершения цикла проверяем, были ли найдены дубликаты
                    if (duplicateIds.length() > 0) {
                        // Удаляем последнюю запятую и пробел
                        duplicateIds.delete(duplicateIds.length() - 2, duplicateIds.length());
                        // Выводим сообщение об ошибке только если найдены дубликаты
                        System.err.println("Записи с идентификаторами " + duplicateIds + " уже существуют в базе данных.");
                        CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Дублирование записи", "Записи с идентификаторами " + duplicateIds + " уже существуют в базе данных.");
                    }

                    // Все данные успешно импортированы
                    System.out.println("Данные успешно импортированы в базу данных.");
                } catch (ClassNotFoundException e) {
                    // Обработка ошибок соединения с базой данных
                    System.err.println("Ошибка при соединении с базой данных: " + e.getMessage());
                    CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Ошибка при соединении с базой данных", "");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (FileNotFoundException e) {
                // Обработка ошибок открытия файла
                System.err.println("Файл не найден: " + e.getMessage());
                CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Файл не найден", "");
            }
        } else {
            // Если файл не выбран, выводим сообщение об ошибке
            System.err.println("Файл не выбран.");
            CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Файл не выбран", "");
        }
    }

    @FXML
    private void importAbonement() {
        // Получаем выбранный файл
        File selectedFile = selectedFileAbonement;

        // Проверяем, что файл выбран
        if (selectedFile != null) {
            // Показываем кнопку "Импорт", так как файл выбран
            importButtonAbonement.setVisible(true);

            try (Scanner scanner = new Scanner(selectedFile)) {
                // Пропускаем первую строку, если это заголовок
                if (scanner.hasNextLine()) {
                    scanner.nextLine(); // пропускаем заголовок
                }

                // Создаем объект базы данных
                DB db = DB.getBase();

                // Создаем строку для хранения совпадающих идентификаторов
                StringBuilder duplicateIds = new StringBuilder();

                // Открываем соединение с базой данных
                try (Connection connection = db.getDbConnection()) {
                    // Читаем данные из файла и вставляем их в базу данных
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine().trim(); // удаляем пробелы в начале и в конце строки
                        if (line.isEmpty()) {
                            continue; // пропускаем пустые строки
                        }
                        // Удаляем кавычки из строки
                        line = line.replaceAll("\"", "");
                        String[] data = line.split(","); // предполагается, что данные разделены запятой

                        // Предполагается, что данные в файле имеют следующий порядок: фамилия, имя, отчество, телефон, email
                        if (data.length == 9) {
                            int id = Integer.parseInt(data[0]);
                            int nominal = Integer.parseInt(data[1]);
                            LocalDate dateOfUse = LocalDate.parse(data[2]);
                            int balance = Integer.parseInt(data[3]);
                            LocalDate dateOfEnd = LocalDate.parse(data[4]);
                            LocalDate dateOfBuy = LocalDate.parse(data[5]);
                            LocalDate dateOfRes = LocalDate.parse(data[6]);
                            int idStatus = Integer.parseInt(data[7]);
                            int idClient = Integer.parseInt(data[8]);

                            // Проверяем, существует ли запись с таким же id
                            if (!db.abonementExists(id)) {
                                // Если записи с таким id не существует, добавляем нового клиента в базу данных
                                db.addNewAbonement(id, nominal, dateOfUse, balance, dateOfEnd, dateOfBuy, dateOfRes, idStatus, idClient);
                                markAbonement.setVisible(true);
                            } else {
                                // Если запись с таким id уже существует, добавляем id в строку с совпадающими идентификаторами
                                duplicateIds.append(id).append(", ");
                            }
                        } else {
                            // Если данные в файле неправильные, выводим сообщение об ошибке
                            System.err.println("Неправильный формат данных в файле: " + line);
                            CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Неправильный формат", "Неправильный формат данных в файле: \n" + line);
                        }
                    }

                    // После завершения цикла проверяем, были ли найдены дубликаты
                    if (duplicateIds.length() > 0) {
                        // Удаляем последнюю запятую и пробел
                        duplicateIds.delete(duplicateIds.length() - 2, duplicateIds.length());
                        // Выводим сообщение об ошибке только если найдены дубликаты
                        System.err.println("Записи с идентификаторами " + duplicateIds + " уже существуют в базе данных.");
                        CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Дублирование записи", "Записи с идентификаторами " + duplicateIds + " уже существуют в базе данных.");
                    }

                    // Все данные успешно импортированы
                    System.out.println("Данные успешно импортированы в базу данных.");
                } catch (ClassNotFoundException e) {
                    // Обработка ошибок соединения с базой данных
                    System.err.println("Ошибка при соединении с базой данных: " + e.getMessage());
                    CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Ошибка при соединении с базой данных", "");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (FileNotFoundException e) {
                // Обработка ошибок открытия файла
                System.err.println("Файл не найден: " + e.getMessage());
                CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Файл не найден", "");
            }
        } else {
            // Если файл не выбран, выводим сообщение об ошибке
            System.err.println("Файл не выбран.");
            CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Файл не выбран", "");
        }
    }

    @FXML
    private void importRace() {
        // Получаем выбранный файл
        File selectedFile = selectedFileRace;

        // Проверяем, что файл выбран
        if (selectedFile != null) {
            // Показываем кнопку "Импорт", так как файл выбран
            importButtonRace.setVisible(true);

            try (Scanner scanner = new Scanner(selectedFile)) {
                // Пропускаем первую строку, если это заголовок
                if (scanner.hasNextLine()) {
                    scanner.nextLine(); // пропускаем заголовок
                }

                // Создаем объект базы данных
                DB db = DB.getBase();

                // Создаем строку для хранения совпадающих идентификаторов
                StringBuilder duplicateIds = new StringBuilder();

                // Открываем соединение с базой данных
                try (Connection connection = db.getDbConnection()) {
                    // Читаем данные из файла и вставляем их в базу данных
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine().trim(); // удаляем пробелы в начале и в конце строки
                        if (line.isEmpty()) {
                            continue; // пропускаем пустые строки
                        }
                        // Удаляем кавычки из строки
                        line = line.replaceAll("\"", "");
                        String[] data = line.split(","); // предполагается, что данные разделены запятой

                        // Предполагается, что данные в файле имеют следующий порядок: фамилия, имя, отчество, телефон, email
                        if (data.length == 5) {
                            int id = Integer.parseInt(data[0]);
                            LocalDate date = LocalDate.parse(data[1]);
                            Integer idCertificate = null; // Устанавливаем значение по умолчанию для id сертификата
                            Integer idAbonement = null; // Устанавливаем значение по умолчанию для id абонемента

                            int spent = Integer.parseInt(data[2]);

                            // Проверяем, не равно ли значение "null" для id сертификата
                            if (!data[3].equalsIgnoreCase("null")) {
                                idCertificate = Integer.parseInt(data[3]);
                            }

                            // Проверяем, не равно ли значение "null" для id абонемента
                            if (!data[4].equalsIgnoreCase("null")) {
                                idAbonement = Integer.parseInt(data[4]);
                            }

                            // Проверяем, существует ли запись с таким же id
                            if (!db.raceExists(id)) {
                                // Если записи с таким id не существует, добавляем нового клиента в базу данных
                                db.addNewRace(id, date, spent, idCertificate, idAbonement);
                                markRace.setVisible(true);
                            } else {
                                // Если запись с таким id уже существует, добавляем id в строку с совпадающими идентификаторами
                                duplicateIds.append(id).append(", ");
                            }
                        } else {
                            // Если данные в файле неправильные, выводим сообщение об ошибке
                            System.err.println("Неправильный формат данных в файле: " + line);
                            CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Неправильный формат", "Неправильный формат данных в файле: \n" + line);
                        }
                    }

                    // После завершения цикла проверяем, были ли найдены дубликаты
                    if (duplicateIds.length() > 0) {
                        // Удаляем последнюю запятую и пробел
                        duplicateIds.delete(duplicateIds.length() - 2, duplicateIds.length());
                        // Выводим сообщение об ошибке только если найдены дубликаты
                        System.err.println("Записи с идентификаторами " + duplicateIds + " уже существуют в базе данных.");
                        CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Дублирование записи", "Записи с идентификаторами " + duplicateIds + " уже существуют в базе данных.");
                    }

                    // Все данные успешно импортированы
                    System.out.println("Данные успешно импортированы в базу данных.");
                } catch (ClassNotFoundException e) {
                    // Обработка ошибок соединения с базой данных
                    System.err.println("Ошибка при соединении с базой данных: " + e.getMessage());
                    CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Ошибка при соединении с базой данных", "");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (FileNotFoundException e) {
                // Обработка ошибок открытия файла
                System.err.println("Файл не найден: " + e.getMessage());
                CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Файл не найден", "");
            }

        } else {
            // Если файл не выбран, выводим сообщение об ошибке
            System.err.println("Файл не выбран.");
            CreateAlert.showAlert(Alert.AlertType.ERROR, "Ошибка", "Файл не выбран", "");
        }
    }
}
