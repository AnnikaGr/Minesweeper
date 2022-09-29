package com.example.game;

import controller.GameController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class Welcome extends Application {


    @FXML private Button startButton;

    private static Stage primaryStage;

    private void setPrimaryStage(Stage stage) {
        Welcome.primaryStage = stage;
    }

    static public Stage getPrimaryStage() {
        return Welcome.primaryStage;
    }



    @Override
    public void start(Stage stage) throws IOException {
        setPrimaryStage(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(Game.class.getResource("welcome-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1820, 980);
        stage.setTitle("Climate Manager");
        stage.setScene(scene);
        stage.show();

/*        stage.setOnCloseRequest(event -> {

            Platform.runLater(new Runnable() {
                public void run() {
                    if (isTimerSet) {
                        timer.cancel();
                    }
                }
            });
        });*/

    }


    public static void main(String[] args) {
        launch();
    }

    @FXML
    public void initialize() {
        startButton.setOnAction(e -> {
            Parent newRoot = null;
            try {
                newRoot = FXMLLoader.load(getClass().getResource("game-view.fxml"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            getPrimaryStage().getScene().setRoot(newRoot);
        });
    }
}