package com.example.game;

import controller.GameController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Board;
import model.Game;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static model.Board.isTimerSet;

public class Welcome extends Application {

    @FXML private Button startButton;

    @FXML private ToggleGroup toggleClimateRelevantSize;
    @FXML private ToggleGroup togglePlanetSize;
    @FXML private Text errorText;

    private Game gameInstance;

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

        FXMLLoader fxmlLoader = new FXMLLoader(Welcome.class.getResource("welcome-view.fxml"));


        Scene scene = new Scene(fxmlLoader.load(), 1820, 980);

        stage.setTitle("Climate Manager");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> {

            Platform.runLater(new Runnable() {
                public void run() {
                    if (isTimerSet) {
                        Board.timer.cancel();
                    }
                }
            });
        });

    }


    public static void main(String[] args) {
        launch();
    }

    @FXML
    public void initialize() {
        // Event Listener for Start Button
        startButton.setOnAction(e -> {
            Parent newRoot = null;

                FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("game-view.fxml"));

            RadioButton selectedPlanetSize = (RadioButton) togglePlanetSize.getSelectedToggle();
                RadioButton selectedClimateRelevantSize = (RadioButton)toggleClimateRelevantSize.getSelectedToggle();
                if(selectedPlanetSize ==null || selectedClimateRelevantSize== null){
                    errorText.setText("Please choose the size of the planet and its climate relevant areas.");
                }
                else{
                    try {
                    gameLoader.setController(new GameController(new Game(selectedPlanetSize.getText().equals("big"), selectedClimateRelevantSize.getText().equals("big"))));
                    newRoot=gameLoader.load();
                    getPrimaryStage().getScene().setRoot(newRoot);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
        });
    }
}