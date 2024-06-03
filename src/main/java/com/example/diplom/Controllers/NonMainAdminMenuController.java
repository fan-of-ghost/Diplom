package com.example.diplom.Controllers;

import com.example.diplom.addLibraries.WindowsActions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

import java.io.IOException;

public class NonMainAdminMenuController {
    @FXML
    Hyperlink linkToAbonements;
    @FXML
    Hyperlink linkToCertificates;

    @FXML
    void moveToAbonements(ActionEvent event) throws IOException {
        WindowsActions.changeWindow(event,"Абонементы (администратор)","nonMainAdminAbonements.fxml");
    }

    @FXML
    void moveToCertificates(ActionEvent event) throws IOException {
        WindowsActions.changeWindow(event,"Сертификаты (администратор)","nonMainAdminCertificates.fxml");
    }

    public void toLogOut(ActionEvent actionEvent) throws IOException {
        WindowsActions.changeWindow(actionEvent,"Авторизация", "login.fxml");
    }
}