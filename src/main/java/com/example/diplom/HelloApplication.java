package com.example.diplom;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Logger.getLogger("javafx.fxml.FXMLLoader").setLevel(Level.SEVERE);
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Авторизация");

        // Установка иконки программы
        Image icon = new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("/Images/carting-icon.png")));
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}