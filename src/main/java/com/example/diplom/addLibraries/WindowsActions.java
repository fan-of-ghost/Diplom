package com.example.diplom.addLibraries;

import com.example.diplom.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class WindowsActions {
    // Метод перехода на другое окно
    // event - event с кнопки
    // title - название окна
    // pageName - fxml файл
    // width - ширина в пикселях
    // height - высота в пикселях
    public static void changeWindow(ActionEvent event, String title, String pageName, int width, int height) throws IOException {
        openWindow(title, pageName, width, height);
        if (event.getSource() instanceof Node) {
            closeWindow(event);
        } else {
            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            stage.close();
        }
    }

    // та же функция, но без заданной ширины и высоты окна
    public static void changeWindow(ActionEvent event, String title, String pageName) throws IOException {
        openWindow(title, pageName);
        if (event.getSource() instanceof Node) {
            closeWindow(event);
        } else {
            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            stage.close();
        }
    }

    // Открытие нового окна
    // title - название окна
    // pageName - fxml файл
    // width - ширина в пикселях
    // height - высота в пикселях
    public static void openWindow(String title, String pageName, int width, int height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(pageName));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(false); // Окно не изменяемо по размеру
        stage.show();
    }

    // та же функция, но без заданной ширины и высоты окна
    public static void openWindow(String title, String pageName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(pageName));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(false); // Окно не изменяемо по размеру
        stage.show();
    }

    // Закрытие текущего окна
    // event - event с кнопки
    public static void closeWindow(ActionEvent event) {
        // (Stage) - приводим результат ((Node) event.getSource()).getScene().getWindow() к классу Stage
        // Node - максимально абстрактный класс графического элемента
        // event - как Node, только для события при нажатии кнопки
        // getSource() - получить объект, который инициировал данное событие (event)
        // getScene() - получить сцену, на которой находится объект, полученный из getSource()
        // getWindow() - получить окно, в котором находится сцена, полученная из getScene()
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // Закрываем текущее окно
        currentStage.close();
    }

    // Открытие модального окна
    // title - название окна
    // pageName - fxml файл
    // width - ширина в пикселях
    // height - высота в пикселях
    public static void openModalWindow(String title, String pageName, int width, int height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(pageName));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        Stage stage = new Stage();

        // Устанавливаем окну модальность
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(false); // Окно не изменяемо по размеру
        stage.showAndWait();
    }

    public static void openModalWindow(String title, String pageName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(pageName));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();

        // Устанавливаем окну модальность
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(false); // Окно не изменяемо по размеру
        stage.showAndWait();
    }
}
