package com.example.diplom;

import com.example.diplom.Products.Abonement;
import com.example.diplom.Products.Certificate;
import com.example.diplom.Products.Client;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static java.lang.String.valueOf;

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
        List<Abonement> activeAbonements = new ArrayList<>();
        String sql = "SELECT ab.*, st.название AS статус " +
                "FROM `Абонементы` ab " +
                "INNER JOIN `Статусы` st ON ab.id_статуса = st.id_статуса " +
                "WHERE ab.архив = 'не в архиве'";
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
                abonement.setStatus(resultSet.getString("статус"));
                abonement.setIdClient(resultSet.getInt("id_клиента"));
                activeAbonements.add(abonement);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return activeAbonements;
    }


    public List<Abonement> getInactiveAbonements() {
        List<Abonement> abonements = new ArrayList<>();
        String sql = "SELECT ab.*, st.название AS статус " +
                "FROM `Абонементы` ab " +
                "INNER JOIN `Статусы` st ON ab.id_статуса = st.id_статуса " +
                "WHERE ab.архив = 'в архиве'";
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
                abonement.setStatus(resultSet.getString("статус"));
                abonement.setIdClient(resultSet.getInt("id_клиента"));
                abonements.add(abonement);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return abonements;
    }

    public void archiveAbonement(int abonementId) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE `Абонементы` SET архив = 'в архиве' WHERE id_абонемента = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, abonementId);
            statement.executeUpdate();
        }
    }

    public void archiveCertificate(int certificateId) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE `Сертификаты` SET архив = 'в архиве' WHERE id_сертификата = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, certificateId);
            statement.executeUpdate();
        }
    }

    public void unArchiveAbonement(int abonementId) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE `Абонементы` SET архив = 'не в архиве' WHERE id_абонемента = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, abonementId);
            statement.executeUpdate();
        }
    }

    public void unArchiveCertificate(int certificateId) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE `Сертификаты` SET архив = 'не в архиве' WHERE id_сертификата = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, certificateId);
            statement.executeUpdate();
        }
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

    public void addNewClient(int id, String surname, String name, String patronymic, String phoneNumber, String email) {
        String sql = "INSERT INTO `Клиенты` (id_клиента, фамилия, имя, отчество, контактный_телефон, адрес_электронной_почты) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, surname);
            statement.setString(3, name);
            statement.setString(4, patronymic);
            statement.setString(5, phoneNumber);
            statement.setString(6, email);
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

    public List<Certificate> getCertificates() {
        List<Certificate> certificates = new ArrayList<>();
        String sql = "SELECT ser.*, st.название AS статус " +
                "FROM `Сертификаты` ser " +
                "INNER JOIN `Статусы` st ON ser.id_статуса = st.id_статуса " +
                "WHERE ser.архив = 'не в архиве'";
        try (Connection connection = getDbConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                // Создаем объект абонемента и заполняем его данными из результата запроса
                Certificate certificate = new Certificate();
                certificate.setId(resultSet.getInt("id_сертификата"));
                certificate.setNominal(resultSet.getInt("номинал_в_минутах"));
                certificate.setDateOfUse(resultSet.getDate("дата_использования"));
                certificate.setBalance(resultSet.getInt("остаток_в_минутах"));
                certificate.setDateOfBuy(resultSet.getDate("дата_покупки"));
                certificate.setDateOfEnd(resultSet.getDate("дата_истечения"));
                certificate.setStatus(resultSet.getString("статус"));
                certificate.setIdClient(resultSet.getInt("id_клиента"));
                certificates.add(certificate);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return certificates;
    }

    public List<Certificate> getInactiveCertificates() {
        List<Certificate> certificates = new ArrayList<>();
        String sql = "SELECT ser.*, st.название AS статус " +
                "FROM `Сертификаты` ser " +
                "INNER JOIN `Статусы` st ON ser.id_статуса = st.id_статуса " +
                "WHERE ser.архив = 'в архиве'";
        try (Connection connection = getDbConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                // Создаем объект абонемента и заполняем его данными из результата запроса
                Certificate certificate = new Certificate();
                certificate.setId(resultSet.getInt("id_сертификата"));
                certificate.setNominal(resultSet.getInt("номинал_в_минутах"));
                certificate.setDateOfUse(resultSet.getDate("дата_использования"));
                certificate.setBalance(resultSet.getInt("остаток_в_минутах"));
                certificate.setDateOfBuy(resultSet.getDate("дата_покупки"));
                certificate.setDateOfEnd(resultSet.getDate("дата_истечения"));
                certificate.setStatus(resultSet.getString("статус"));
                certificate.setIdClient(resultSet.getInt("id_клиента"));
                certificates.add(certificate);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return certificates;
    }

    public void addCertificate(int nominal, int status, int idClient, Date dateOfBuy) {
        String sql = "INSERT INTO `Сертификаты` (номинал_в_минутах, дата_покупки, дата_истечения, остаток_в_минутах, id_статуса, id_клиента) VALUES (?, ?, ?, ?, ?, ?)";
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

    public Client getClientByAbonementId(int abonementId) {
        String sql = "SELECT c.`id_клиента`, c.`фамилия`, c.`имя`, c.`отчество`, c.`контактный_телефон`, c.`адрес_электронной_почты` " +
                "FROM `Клиенты` c " +
                "JOIN `Абонементы` a ON c.`id_клиента` = a.`id_клиента` " +
                "WHERE a.`id_абонемента` = ?";
        try (PreparedStatement statement = getDbConnection().prepareStatement(sql)) {
            statement.setInt(1, abonementId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id_клиента");
                String surname = resultSet.getString("фамилия");
                String name = resultSet.getString("имя");
                String patronymic = resultSet.getString("отчество");
                String phoneNumber = resultSet.getString("контактный_телефон");
                String email = resultSet.getString("адрес_электронной_почты");

                return new Client(id, surname, name, patronymic, phoneNumber, email);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Client getClientByCertificateId(int certificateId) {
        String sql = "SELECT c.`id_клиента`, c.`фамилия`, c.`имя`, c.`отчество`, c.`контактный_телефон`, c.`адрес_электронной_почты` " +
                "FROM `Клиенты` c " +
                "JOIN `Сертификаты` cer ON c.`id_клиента` = cer.`id_клиента` " +
                "WHERE cer.`id_сертификата` = ?";
        try (PreparedStatement statement = getDbConnection().prepareStatement(sql)) {
            statement.setInt(1, certificateId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id_клиента");
                String surname = resultSet.getString("фамилия");
                String name = resultSet.getString("имя");
                String patronymic = resultSet.getString("отчество");
                String phoneNumber = resultSet.getString("контактный_телефон");
                String email = resultSet.getString("адрес_электронной_почты");

                return new Client(id, surname, name, patronymic, phoneNumber, email);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean clientExists(int id) {
        String query = "SELECT COUNT(*) FROM Клиенты WHERE id_клиента = ?";
        try (Connection conn = getDbConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(); // Обработка ошибок (логирование и т.д.)
        }
        return false;
    }

    public boolean certificateExists(int id) {
        String query = "SELECT COUNT(*) FROM Сертификаты WHERE id_сертификата = ?";
        try (Connection conn = getDbConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(); // Обработка ошибок (логирование и т.д.)
        }
        return false;
    }

    public boolean abonementExists(int id) {
        String query = "SELECT COUNT(*) FROM Абонементы WHERE id_абонемента = ?";
        try (Connection conn = getDbConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(); // Обработка ошибок (логирование и т.д.)
        }
        return false;
    }

    public boolean abonementRaceExists(int id) {
        String query = "SELECT COUNT(*) FROM График_абонементов WHERE id_графика = ?";
        try (Connection conn = getDbConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(); // Обработка ошибок (логирование и т.д.)
        }
        return false;
    }

    public boolean certificateRaceExists(int id) {
        String query = "SELECT COUNT(*) FROM График_сертификатов WHERE id_графика = ?";
        try (Connection conn = getDbConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(); // Обработка ошибок (логирование и т.д.)
        }
        return false;
    }


    public void addNewCertificate(int id, int nominal, LocalDate dateOfUse, int balance, LocalDate dateOfEnd, LocalDate dateOfBuy, int idStatus, int idClient) {
        String sql = "INSERT INTO `Сертификаты` (id_сертификата, номинал_в_минутах, дата_использования, остаток_в_минутах, дата_истечения, дата_покупки, id_статуса, id_клиента) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setInt(2, nominal);
            statement.setDate(3, Date.valueOf(dateOfUse));
            statement.setInt(4, balance);
            statement.setDate(5, Date.valueOf(dateOfEnd));
            statement.setDate(6, Date.valueOf(dateOfBuy));
            statement.setInt(7, idStatus);
            statement.setInt(8, idClient);
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addNewAbonement(int id, int nominal, LocalDate dateOfUse, int balance, LocalDate dateOfEnd, LocalDate dateOfBuy, LocalDate dateOfRes, int idStatus, int idClient) {
        String sql = "INSERT INTO `Абонементы` (id_абонемента, номинал_в_минутах, дата_использования, остаток_в_минутах, дата_истечения, дата_покупки, дата_продления, id_статуса, id_клиента) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setInt(2, nominal);
            statement.setDate(3, Date.valueOf(dateOfUse));
            statement.setInt(4, balance);
            statement.setDate(5, Date.valueOf(dateOfEnd));
            statement.setDate(6, Date.valueOf(dateOfBuy));
            statement.setDate(7, Date.valueOf(dateOfRes));
            statement.setInt(8, idStatus);
            statement.setInt(9, idClient);
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addNewAbonementRace(int id, LocalDate date, int spentTime, int idAbonement) {
        String sql = "INSERT INTO `График_абонементов` (id_графика, дата_использования, затраченное_время_в_минутах, id_абонемента) VALUES (?, ?, ?, ?)";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setDate(2, Date.valueOf(date));
            statement.setInt(3, spentTime);
            statement.setInt(4, idAbonement);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при добавлении нового заезда в график абонементов в базу данных", e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при подключении к базе данных", e);
        }
    }

    public void addNewCertificateRace(int id, LocalDate date, int spentTime, int idCertificate) {
        String sql = "INSERT INTO `График_сертификатов` (id_графика, дата_использования, затраченное_время_в_минутах, id_сертификата) VALUES (?, ?, ?, ?)";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setDate(2, Date.valueOf(date));
            statement.setInt(3, spentTime);
            statement.setInt(4, idCertificate);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при добавлении нового заезда в график сертификатов в базу данных", e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при подключении к базе данных", e);
        }
    }

    public String checkStateByIdAbonement(int id) {
        String sql = "SELECT `состояние` FROM `Абонементы` WHERE `id_абонемента` = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("состояние");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ""; // Если не найдено, возвращаем пустоту
    }

    public String checkStateByIdCertificate(int id) {
        String sql = "SELECT `состояние` FROM `Сертификаты` WHERE `id_сертификата` = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("состояние");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ""; // Если не найдено, возвращаем пустоту
    }

    public Set<Client> getClientsForPeriodAbonement(LocalDate startDate, LocalDate endDate) {
        Set<Client> clients = new HashSet<>();
        String sql = "SELECT DISTINCT c.* " +
                "FROM `Абонементы` a " +
                "JOIN `Клиенты` c ON a.id_клиента = c.id_клиента " +
                "WHERE a.дата_использования BETWEEN ? AND ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id_клиента");
                String lastName = resultSet.getString("фамилия");
                String firstName = resultSet.getString("имя");
                String middleName = resultSet.getString("отчество");
                String phone = resultSet.getString("контактный_телефон");
                String email = resultSet.getString("адрес_электронной_почты");

                // Создание объекта клиента и добавление его в множество
                clients.add(new Client(id, lastName, firstName, middleName, phone, email));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clients;
    }

    public Set<Client> getClientsForPeriodCertificate(LocalDate startDate, LocalDate endDate) {
        Set<Client> clients = new HashSet<>();
        String sql = "SELECT DISTINCT c.* " +
                "FROM `Сертификаты` ser " +
                "JOIN `Клиенты` c ON ser.id_клиента = c.id_клиента " +
                "WHERE ser.дата_использования BETWEEN ? AND ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id_клиента");
                String lastName = resultSet.getString("фамилия");
                String firstName = resultSet.getString("имя");
                String middleName = resultSet.getString("отчество");
                String phone = resultSet.getString("контактный_телефон");
                String email = resultSet.getString("адрес_электронной_почты");

                // Создание объекта клиента и добавление его в множество
                clients.add(new Client(id, lastName, firstName, middleName, phone, email));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clients;
    }

    public int getBalanceAbonement(int id) {
        String sql = "SELECT остаток_в_минутах FROM Абонементы WHERE id_абонемента = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("остаток_в_минутах");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0; // Если не найдено, возвращаем 0 или обрабатываем по-другому
    }

    public int getBalanceCertificate(int id) {
        String sql = "SELECT остаток_в_минутах FROM Сертификаты WHERE id_сертификата = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("остаток_в_минутах");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0; // Если не найдено, возвращаем 0 или обрабатываем по-другому
    }

    public void insertReservationAbonement(int abonementId, LocalDate reservationDate) throws SQLException {
        String sql = "INSERT INTO График_абонементов (id_абонемента, дата_использования) VALUES (?, ?)";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, abonementId);
            statement.setDate(2, java.sql.Date.valueOf(reservationDate));
            statement.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver не найден", e);
        }
    }

    public void insertReservationCertificate(int certificateId, LocalDate reservationDate) throws SQLException {
        String sql = "INSERT INTO График_сертификатов (id_сертификата, дата_использования) VALUES (?, ?)";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, certificateId);
            statement.setDate(2, java.sql.Date.valueOf(reservationDate));
            statement.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver не найден", e);
        }
    }

    public void updateAbonement(int abonementId, int minutesUsed) throws SQLException {
        String sql = "UPDATE Абонементы SET остаток_в_минутах = остаток_в_минутах - ? WHERE id_абонемента = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, minutesUsed);
            statement.setInt(2, abonementId);
            statement.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver не найден", e);
        }
    }

    public void updateCertificate(int certificateId, int minutesUsed) throws SQLException {
        String sql = "UPDATE Сертификаты SET остаток_в_минутах = остаток_в_минутах - ? WHERE id_сертификата = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, minutesUsed);
            statement.setInt(2, certificateId);
            statement.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver не найден", e);
        }
    }

    public boolean extensionAbonement(int abonementId, int additionalMinutes) {
        String sql = "UPDATE `Абонементы` SET `остаток_в_минутах` = `остаток_в_минутах` + ?, `дата_продления` = NOW() WHERE `id_абонемента` = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, additionalMinutes);
            statement.setInt(2, abonementId);
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Abonement> getActiveAbonements() {
        List<Abonement> activeAbonements = new ArrayList<>();
        String sql = "SELECT ab.*, st.название AS статус " +
                "FROM `Абонементы` ab " +
                "INNER JOIN `Статусы` st ON ab.id_статуса = st.id_статуса " +
                "WHERE ab.архив = 'не в архиве' AND ab.состояние = 'active'";
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
                abonement.setStatus(resultSet.getString("статус"));
                abonement.setIdClient(resultSet.getInt("id_клиента"));
                activeAbonements.add(abonement);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return activeAbonements;
    }

    public List<Certificate> getActiveCertificates() {
        List<Certificate> activeCertificates = new ArrayList<>();
        String sql = "SELECT ser.*, st.название AS статус " +
                "FROM `Сертификаты` ser " +
                "INNER JOIN `Статусы` st ON ser.id_статуса = st.id_статуса " +
                "WHERE ser.архив = 'не в архиве' AND ser.состояние = 'active'";
        try (Connection connection = getDbConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                // Создаем объект сертификата и заполняем его данными из результата запроса
                Certificate certificate = new Certificate();
                certificate.setId(resultSet.getInt("id_сертификата"));
                certificate.setNominal(resultSet.getInt("номинал_в_минутах"));
                certificate.setDateOfUse(resultSet.getDate("дата_использования"));
                certificate.setBalance(resultSet.getInt("остаток_в_минутах"));
                certificate.setDateOfBuy(resultSet.getDate("дата_покупки"));
                certificate.setDateOfEnd(resultSet.getDate("дата_истечения"));
                certificate.setStatus(resultSet.getString("статус"));
                certificate.setIdClient(resultSet.getInt("id_клиента"));
                activeCertificates.add(certificate);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return activeCertificates;
    }

    public boolean extendAbonement(int abonementId, int days) {
        String sql = "UPDATE `Абонементы` SET `дата_истечения` = DATE_ADD(`дата_истечения`, INTERVAL ? DAY), `состояние` = 'active' WHERE `id_абонемента` = ?";
        try (Connection connection = getDbConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, days);
            statement.setInt(2, abonementId);
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


}