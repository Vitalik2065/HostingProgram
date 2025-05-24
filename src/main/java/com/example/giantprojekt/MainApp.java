// File: src/main/java/com/example/giantprojekt/MainApp.java
package com.example.giantprojekt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.example.giantprojekt.service.DiscordServices.DiscordService;

import javax.security.auth.login.LoginException;
import java.util.ResourceBundle;

public class MainApp extends Application {

    private static final String API_KEY = ResourceBundle
            .getBundle("application")
            .getString("discord.api.token");

    public static void main(String[] args) {
        // 1) Запускаем Discord-бота
        try {
            new DiscordService().startBot(API_KEY);
        } catch (LoginException e) {
            e.printStackTrace();
        }

        // 2) А теперь – JavaFX
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MainView.fxml")
        );
        Parent root = loader.load();
        primaryStage.setTitle("GiantProjekt");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();
    }
}
