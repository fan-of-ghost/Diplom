package com.example.diplom.Controllers;

import com.example.diplom.DB;
import com.example.diplom.Products.Client;
import com.example.diplom.addLibraries.DataExchanger;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class ClientInfoCertificatesController {
    @FXML
    private Text numberField;
    @FXML
    private Text fioField;
    @FXML
    private Text contactNumberField;
    @FXML
    private Text mailField;

    public void initialize() {
        // Получаем id абонемента из DataExchanger
        DataExchanger dataExchanger = DataExchanger.getInstance();
        int certificateId = dataExchanger.getId();

        // Получаем информацию о клиенте по id абонемента
        Client client = getClientByCertificateId(certificateId);

        // Вставляем данные о клиенте в соответствующие поля
        setClientInfo(client);
    }

    private Client getClientByCertificateId(int certificateId) {
        DB db = DB.getBase();
        return db.getClientByCertificateId(certificateId);
    }

    public void setClientInfo(Client client) {
        // Вставляем данные о клиенте в соответствующие поля
        if (client != null) {
            numberField.setText(String.valueOf(client.getId()));
            fioField.setText(client.getSurname() + " " + client.getName() + " " + client.getPatronymic());
            contactNumberField.setText(client.getPhoneNumber());
            mailField.setText(client.getEmail());
        } else {
            // Если клиент не найден, выводим сообщение об ошибке
            System.out.println("Клиент не найден");
        }
    }
}
