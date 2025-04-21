package com.example.giantprojekt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.example.giantprojekt.service.DiscordService;

import javax.security.auth.login.LoginException;
import java.util.ResourceBundle;


public class MainApp extends Application {

    private static String API_KEY = ResourceBundle
            .getBundle("application")
            .getString("discord.api.token");

    public static void main(String[] args) {
        // сначала запускаем бота
        try {
            new DiscordService().startBot(API_KEY);
        } catch (LoginException e) {
            e.printStackTrace();
        }

        //launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

    }

    /*Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        primaryStage.setTitle("GiantProjekt");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }*/
}








/*public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}*/
