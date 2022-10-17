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

import static model.Board.isTimerSet;

public class Main extends Application {

    private static Stage primaryStage;
    @FXML
    private Button startButton;
    @FXML
    private ToggleGroup toggleClimateRelevantSize;
    @FXML
    private ToggleGroup togglePlanetSize;
    @FXML
    private Text errorText;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        setPrimaryStage(stage);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("welcome-view.fxml"));

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

    @FXML
    public void initialize() {
        // Event Listener for Start Button
        startButton.setOnAction(e -> {
            startGame();
        });
    }

    // -- Event Handling --------------------------------------------------------------------------------------------

    private void startGame() {
        Parent newRoot = null;
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("game-view.fxml"));

        // get user input
        RadioButton selectedPlanetSize = (RadioButton) togglePlanetSize.getSelectedToggle();
        RadioButton selectedClimateRelevantSize = (RadioButton) toggleClimateRelevantSize.getSelectedToggle();
        if (selectedPlanetSize == null || selectedClimateRelevantSize == null) {
            // handle no user input
            errorText.setText("Please choose the size of the planet and its climate relevant areas.");
        } else {
            // open game view
            try {
                gameLoader.setController(new GameController(
                        new Game(selectedPlanetSize.getText().equals("big"), selectedClimateRelevantSize.getText().equals("big"))));
                newRoot = gameLoader.load();
                getPrimaryStage().getScene().setRoot(newRoot);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // -- Getter and Setter -------------------------------------------------------------------------------------------

    static public Stage getPrimaryStage() {
        return Main.primaryStage;
    }

    private void setPrimaryStage(Stage stage) {
        Main.primaryStage = stage;
    }
}