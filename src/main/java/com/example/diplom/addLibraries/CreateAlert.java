package com.example.diplom.addLibraries;

import javafx.scene.control.Alert;

public class CreateAlert {
    //Метод создания и вывода предупреждения
    //type - тип предупреждения: Alert.AlertType.ERROR, Alert.AlertType.WARNING и т.д.
    //title - название окна
    //headerText - текст заголовка
    //contentText - основной текст
    public static void showAlert (Alert.AlertType type, String title, String headerText, String contentText) {
        //Создаём объект предупреждения и указываем его тип
        Alert alert = new Alert(type);
        //Задаём название окна
        alert.setTitle(title);
        //Задаём текст заголовка
        alert.setHeaderText(headerText);
        //Задаём основной текст
        alert.setContentText(contentText);
        //Показать предупреждение
        alert.showAndWait();
    }
}
