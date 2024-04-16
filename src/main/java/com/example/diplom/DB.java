package com.example.diplom;

import com.example.diplom.Products.Abonement;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DB {
    private DB() {}
    private static DB base;
    public static DB getBase() {
        if (base==null) {
            base = new DB();
        }
        return base;
    }

    private final String HOST = "127.0.0.1";
    private final String PORT = "3306";
    private final String DB_NAME = "Картинг";
    private final String LOGIN = "root";
    private final String PASS = "";
    private Connection dbConn = null;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connStr = "jdbc:mysql://localhost" + ":" + PORT + "/" + DB_NAME;
        Class.forName("com.mysql.cj.jdbc.Driver");
        dbConn = DriverManager.getConnection(connStr, LOGIN, PASS);
        return dbConn;
    }

    public int checkRole(String login, String password) throws ClassNotFoundException {
        String sql = "SELECT `id_роли` FROM `Администраторы` WHERE `логин` =? AND `пароль`=?";
        try (PreparedStatement statement = getDbConnection().prepareStatement(sql)) {
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                int roleId = result.getInt("id_роли");
                if (roleId == 1) {
                    return 1;
                } else if (roleId == 2) {
                    return 2;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0; // Возвращаемое значение по умолчанию или обработка других случаев
    }

    public List<Abonement> getAbonements() {
        List<Abonement> abonements = new ArrayList<>();
        String sql = "SELECT * FROM `Абонементы`";
        try (Connection connection = getDbConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                // Создаем объект абонемента и заполняем его данными из результата запроса
                Abonement abonement = new Abonement();
                abonement.setId(resultSet.getInt("id_абонемента"));
                abonement.setNominal(resultSet.getInt("номинал_в_минутах"));
                abonement.setDateOfUse(resultSet.getDate("дата_использования"));
                abonement.setBalance(resultSet.getInt("остаток_в_минутах"));
                abonement.setDateOfBuy(resultSet.getDate("дата_покупки"));
                abonement.setDateOfEnd(resultSet.getDate("дата_истечения"));
                abonement.setDateOfRes(resultSet.getDate("дата_продления"));
                abonement.setStatus(resultSet.getString("id_статуса"));
                abonement.setIdClient(resultSet.getInt("id_клиента"));
                abonements.add(abonement);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return abonements;
    }

    public void addNewClient(String surname, String name, String patronymic, String phoneNumber, String email) {
        String sql = "INSERT INTO `Клиенты` (фамилия, имя, отчество, контактный_телефон, адрес_электронной_почты) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, surname);
            statement.setString(2, name);
            statement.setString(3, patronymic);
            statement.setString(4, phoneNumber);
            statement.setString(5, email);
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addAbonement(int nominal, int status, int idClient, Date dateOfBuy) {
        String sql = "INSERT INTO `Абонементы` (номинал_в_минутах, дата_покупки, дата_истечения, остаток_в_минутах, id_статуса, id_клиента) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // Установка параметров запроса
            statement.setInt(1, nominal);
            statement.setDate(2, dateOfBuy);

            // Расчет даты истечения: добавляем к дате покупки 1 год
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateOfBuy);
            calendar.add(Calendar.YEAR, 1);
            java.sql.Date dateOfEnd = new java.sql.Date(calendar.getTimeInMillis());

            statement.setDate(3, dateOfEnd);
            statement.setInt(4, nominal); // Номинал также используется для остатка
            statement.setInt(5, status);
            statement.setInt(6, idClient);



            // Выполнение запроса
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getStatusIdByName(String statusName) {
        String sql = "SELECT id_статуса FROM Статусы WHERE название = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, statusName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id_статуса");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0; // Если не найдено, возвращаем 0 или обрабатываем по-другому
    }

    public int getClientIdByEmail(String email) {
        String sql = "SELECT id_клиента FROM Клиенты WHERE адрес_электронной_почты = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id_клиента");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0; // Если не найдено, возвращаем 0 или обрабатываем по-другому
    }

}