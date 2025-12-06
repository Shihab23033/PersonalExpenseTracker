package com.personalexpensetracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/personalexpensetracker/fxml/main.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 650);
        scene.getStylesheets().add(getClass().getResource("/com/personalexpensetracker/css/style.css").toExternalForm());

        stage.setTitle("Personal Expense Tracker");
        Image icon = new Image(getClass().getResourceAsStream("/com/personalexpensetracker/icon.jpg"));
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}