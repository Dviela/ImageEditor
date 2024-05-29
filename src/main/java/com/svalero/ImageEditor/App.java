package com.svalero.ImageEditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application {

    @Override
    public void init() throws Exception {
        System.out.println("Starting ImageEditor...");
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/svalero/ImageEditor/vista.fxml")));
        primaryStage.setTitle("ImageEditor");
        primaryStage.setScene(new Scene(root, 800, 650));
        primaryStage.show();
    }
    @Override
    public void stop() throws Exception {
        System.out.println("Closing ImageEditor.");
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
